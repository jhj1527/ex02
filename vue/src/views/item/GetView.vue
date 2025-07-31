<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { useRouter, onBeforeRouteLeave } from 'vue-router';
  import { onMounted, reactive, ref, computed, defineProps, watch } from 'vue';

  const store = useStore();
  const { member, cart } = storeToRefs(store);
  const router = useRouter();
  const props = defineProps(["ino"]);
  const result = ref({});
  const input = reactive({
    ino: props.ino,
    id: member.value.id || "",
    quantity: 0,
    price: "",
  });
  let param = {};
  const mainSrc = ref([]);
  const subSrc = ref([]);
  const rating = ref(4)
  const reviewRating = ref(4)
  const tab = ref('description')
  const reviewDate = 'April 12, 2024'
  const isDisabled = reactive({
    minus: false,
    plus: false,
    addCart: false,
  });

  onMounted(() => {
    get();
  });

  onBeforeRouteLeave((to, from, next) => {
    mainSrc.value.forEach((item) => {
      URL.revokeObjectURL(item);
    });

    subSrc.value.forEach((item) => {
      URL.revokeObjectURL(item);
    });

    next();
  });

  watch(() => input.quantity,
    (newValue, oldValue) => {
      result.value.amount = result.value.amount + oldValue - newValue;
      result.value.amount === 0 ? isDisabled.plus = true : isDisabled.plus = false;
      newValue <= 1 ? isDisabled.minus = true : isDisabled.minus = false;
    },
    { deep: true },
  );

  const priceFormat = computed(() => {
    return price => price > 0 ? price.toLocaleString() : 0
  });

  const increment = computed(() => {
    return value => input.quantity++;
  });

  const decrement = computed(() => {
    return value => input.quantity > 1 ? input.quantity-- : input.quantity = 1;
  });

  const realPrice = computed(() => {
    return result.value.discount > 0 ? 
      Math.round(result.value.price * (1 - result.value.discount / 100)) * input.quantity 
      : result.value.price * input.quantity
  });

  const get = async () => {
    try {
      const res = await commonApi(`/api/item/${props.ino}`, 'GET');

      // console.log(res);

      if (res.status == 200) {
          result.value = res.data;

          if (result.value.amount === 0) {
            isDisabled.addCart = true;
            isDisabled.plus = true;
            isDisabled.minus = true;
            input.quantity = 0;

          } else {
            input.quantity = 1;
          }
          
          if (result.value.category === 'clothes') {
            result.value.category = '의류';

          } else if (result.value.category === 'cap') {
            result.value.category = '모자';

          } else if (result.value.category === 'shoes') {
            result.value.category = '신발';
          }
          
          result.value.attachList.forEach((item, i) => {
            getImage(item, i);
          });
      }
      
    } catch (e) {
      console.log(e);
    }
  };

  const getImage = async (item, i) => {
    try {
      param = {};
      param.filePath = item.filePath;
      param.fileName = item.fileName;
  
      const res = await commonApi("/api/file/getFile", "get", param);
      // console.log(res.data);
      
      const url = URL.createObjectURL(res.data); // url 생성
      if (item.fileName.startsWith("main")) {
        mainSrc.value[0] = url;
        subSrc.value.unshift(url); // 배열 맨앞에 추가
        
      } else {
        subSrc.value.push(url);
        // subSrc.value[i] = url;
      }
      
    } catch (e) { 
      console.log(e);
    }
  };

  const addCart = async () => {
    try {
      input.price = realPrice.value;

      // console.log(input);

      const res = await commonApi("/api/cart/insert", "post", input);

      if (res.status === 201 || res.status === 200) {
        alert("insert");
        store.getCartCount(input.id);

      } else if (res.status === 400) {
        alert(res.data.message);
      }
      
    } catch (e) { 
      console.log(e);
    }
  };
 
</script>

<template>
  <v-container class="py-8">
    <v-row>
      <!-- Main Image -->
      <v-col cols="12" md="6">
        <v-img
          :src="mainSrc[0]"
          height="400"
          cover
          class="bg-grey-lighten-2 rounded-lg"
        ></v-img>
        
        <!-- Thumbnail Images -->
        <v-row v-if="subSrc.length > 1" class="mt-4">
          <v-col v-for="(src, i) in subSrc" :key="i" cols="3">
            <v-img
              :src="src"
              :width="100"
              :height="100"
              cover
              class="bg-grey-lighten-2 rounded cursor-pointer"
              @click="mainSrc[0] = src"
            ></v-img>
          </v-col>
        </v-row>
      </v-col>

      <!-- Product Info -->
      <v-col cols="12" md="6">
        <h2 class="font-weight-bold mb-2">{{ result.name }}</h2>
        <div class="text-grey mb-2">Category: {{ result.category }}</div>
        <div class="text-h5 font-weight-bold mb-1">
          <span v-if="result.discount && result.discount > 0" class="text-error mr-2">
            {{ result.discount }}%
          </span>
        </div>
        <div class="">
          <span v-if="result.discount && result.discount > 0" class="mr-2" style="text-decoration: line-through; color: #b0b0b0;">
            {{ priceFormat(result.price) }} 
          </span>
          <span>
            {{ priceFormat(result.discount && result.discount > 0 ? Math.round(result.price * (1 - result.discount / 100)) : result.price) }} 
          </span> 
        </div>
        <v-rating
          v-model="rating"
          color="amber"
          background-color="grey lighten-2"
          half-increments
          readonly
          size="24"
          class="mb-4"
        ></v-rating>
        <div class="mb-3">
          재고 : {{ result.amount }}
        </div>
        <div class="mb-4 text-grey">
          test
        </div>
        <div class="d-flex align-center mb-4">
          <v-btn icon @click="decrement" :disabled="isDisabled.minus">
            <v-icon>mdi-minus</v-icon>
          </v-btn>
          <span class="mx-3 text-h6">{{ input.quantity }}</span>
          <!-- :disabled="result.amount === 0 ? true : false" -->
          <v-btn icon @click="increment" :disabled="isDisabled.plus">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </div>
        <v-btn 
          @click="addCart" 
          color="success" 
          variant="outlined" 
          prepend-icon="mdi-cart" 
          class="px-8"
          :disabled="isDisabled.addCart"
          >{{ isDisabled.addCart ? 'Sold Out' : 'Add to cart' }} 
        </v-btn>
      </v-col>
    </v-row>

    <!-- Tabs for Description and Reviews -->
    <v-tabs v-model="tab" class="mt-10">
      <v-tab value="description">Description</v-tab>
      <v-tab value="reviews">Reviews</v-tab>
    </v-tabs>
    <v-window v-model="tab" class="mt-4">
      <v-window-item value="description">
        <div>
          {{ result.content || 'No description available.' }}
        </div>
      </v-window-item>
      <v-window-item value="reviews">
        <v-divider class="mb-4"></v-divider>
        <v-row>
          <v-col cols="12" md="1" class="d-flex justify-center">
            <v-avatar size="56">
              <v-icon size="56" color="grey lighten-1">mdi-account</v-icon>
            </v-avatar>
          </v-col>
          <v-col cols="12" md="11">
            <div class="d-flex align-center">
              <span class="font-weight-bold mr-2">Jason Smith</span>
              <span class="text-grey text-caption">{{ reviewDate }}</span>
              <v-rating
                v-model="reviewRating"
                color="amber"
                background-color="grey lighten-2"
                half-increments
                readonly
                size="20"
                class="ml-auto"
              ></v-rating>
            </div>
            <div class="text-grey mt-1">
              The generated Lorem Ipsum is therefore always free from repetition injected humour, or non-characteristic words etc. Susp endisse ultricies nisi vel quam suscipit
            </div>
          </v-col>
        </v-row>
      </v-window-item>
    </v-window>
  </v-container>
</template>

<style scoped>
.text-grey {
  color: #757575;
}
</style>