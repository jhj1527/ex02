<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { RouterLink, useRouter, onBeforeRouteLeave } from 'vue-router';
  import { onMounted, reactive, ref, computed } from 'vue';

  const store = useStore();
  const { member, cart, itemIno } = storeToRefs(store);
  const router = useRouter();
  const result = ref([]);
  const page = ref({});
  const param = reactive({
    pageNum: 1,
    amount: 10,
    type: "all",
    keyword: "",
  });
  const input = reactive({
    ino: "",
    id: "",
    price: "",
    quantity: "",
  });
  const activeTab = ref('all');
  const mainSrc = ref([]);
  let numbers = [];
  let params = {};
  
  onMounted(() => {
    getList();
  });

  const priceFormat = computed(() => {
    return price => price > 0 ? price.toLocaleString() : 0
  });

  const discountPrice = computed(() => {
    return item => item.discount > 0 ? item.price * (100 - item.discount) / 100 : item.price
  });

  const getList = async () => {
    try {
      const res = await commonApi("/api/item/list", "get", param);
      result.value = res.data.list;
      page.value = res.data.page;

      // console.log(result.value);
      // console.log(page.value);

      numbers = [];

      for (let i = page.value.startPage; i <= page.value.endPage; i++) {
        numbers.push(i);
      }

      // console.log(numbers.length);

      result.value.forEach((item, idx) => {
        if (item.attachList.length > 0) {
          getImage(item.attachList[0], idx);
        }
      });

    } catch (e) {
      console.log(e);
    }
  };

  const getImage = async (item, i) => {
    try {
      params = {};
      params.filePath = item.filePath;
      params.fileName = item.fileName;
  
      const res = await commonApi("/api/file/getFile", "get", params);
      // console.log(res.data);
      
      const url = URL.createObjectURL(res.data); // url 생성
 
      // mainSrc.value.push(url);
      mainSrc.value[i] = url;
      // mainImageRefs.value[mainSrc.value.length - 1].src = url;

    } catch (e) { 
      console.log(e);
    }
  };

  const addCart = async(item) => {
    if (member.value.id === null || member.value.id === "") {
      alert("로그인 후 이용 가능합니다.");
      router.push("/member/login");
      return;
    }

    input.ino = item.ino;
    input.id = member.value.id;
    input.price = discountPrice.value(item);
    input.quantity = 1; 

    console.log(input);
    
    const res = await commonApi("/api/cart/insert", "post", input);

    if (res.status === 201 || res.status === 200) {
      alert("insert");
      store.getCartCount(input.id);

    } else if (res.status === 400) {
      alert(res.data.message);
    }
  };

  const pageClick = (pageNum) => {
    param.value.pageNum = pageNum;
    getList();
  };

  const buyitem = (item) => {
    itemIno.value = null;
    itemIno.value = item.ino;

    router.push("/order/insert");
  };

  const tabChannge = (value) => {
    param.type = value;
    param.pageNum = 1;
    getList();
  };

  const btnChannge = (idx) => {
    mainSrc.value.forEach((item) => {
      URL.revokeObjectURL(item);
    });
    mainSrc.value = [];

    if (idx === 0) {
      result.value.sort((a, b) => {
        return new Date(a.regDate).getTime() - new Date(b.regDate).getTime()
      }).reverse(); 

    } else if (idx === 1) {
      result.value.sort((a, b) => a.price - b.price);

    } else if (idx === 2) {
      result.value.sort((a, b) => b.price - a.price);
    }

    result.value.forEach((item, idx) => {
      if (item.attachList.length > 0) {
        getImage(item.attachList[0], idx);
      }
    });
  };
</script>

<template>
  <v-container>
    <!-- Tab navigation -->
    <v-tabs v-model="activeTab" @update:model-value="tabChannge" class="mb-4 pa-0 ma-0">
      <v-tab value="all">전체</v-tab>
      <v-tab value="clothes">의류</v-tab>
      <v-tab value="cap">모자</v-tab>
      <v-tab value="shoes">신발</v-tab>
    </v-tabs>

    <!-- Item count and sort options -->
    <v-row class="mb-4" align="center">
      <v-col cols="6">
        <div class="text-body-1 pa-0 ma-3">총 {{ page.total }}건</div>
      </v-col>
      <v-col cols="6" class="text-right">
        <v-btn-toggle @update:model-value="btnChannge">
          <v-btn variant="text">신상품순</v-btn>
          <!-- <v-btn variant="text">인기상품순</v-btn> -->
          <v-btn variant="text">낮은가격순</v-btn>
          <v-btn variant="text">높은가격순</v-btn>
        </v-btn-toggle>
      </v-col>
    </v-row>

    <!-- Product grid -->
    <v-row>
      <v-col v-for="(item, i) in result" :key="item.ino" cols="10" sm="5" md="2" lg="2" class="pa-1 ma-3">
        <v-card class="mx-auto" link>
          <v-img 
            v-if="item.attachList.length > 0"
            :src="mainSrc[i]"
            :width="300"
            :height="100"
            @click="router.push(`/item/get/${item.ino}`)"
            cover
          ></v-img>
          <v-card-title class="text-subtitle-2">{{ item.name }}</v-card-title>
          <v-card-text>
            <!-- <v-chip v-if="item.isNew" color="grey-darken-1" size="x-small" class="mb-2">SALE</v-chip> -->
            <div class="text-body-2 mb-1">{{ priceFormat(item.price) }}원</div>
            <div v-if="item.discount > 0" class="d-flex align-center">
              <span class="text-red">{{ item.discount }}%</span>
              <span class="ml-2 text-subtitle-1">{{ priceFormat(discountPrice(item)) }}원</span>
            </div>
            <div v-else class="d-flex align-center">
              <span class="text-red"><br></span>
              <span class="ml-2 text-subtitle-1"><br></span>
            </div>
            <v-btn 
              @click="addCart(item)" 
              :disabled="item.amount === 0 ? true : false" 
              color="primary" 
              class="mt-2" 
              block
              :text="item.amount === 0 ? '품절' : '장바구니'"
            ></v-btn>
            <v-btn
              @click="buyitem(item)"
              :disabled="item.amount === 0 ? true : false" 
              color="grey-darken" 
              class="mt-2" 
              block 
              text="즉시구매"
            ></v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- 페이지네이션 -->
    <div class="text-center mt-4">
      <v-pagination
        v-model="param.pageNum"
        :length="numbers.length"
        class="my-4"
        @update:model-value="pageClick"
      ></v-pagination>
    </div>
  </v-container>
</template>

<style scoped>
  .v-card-title {
    font-size: 0.9rem !important;
    line-height: 1.4;
  }
</style>