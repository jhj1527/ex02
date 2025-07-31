<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { RouterLink, useRouter, onBeforeRouteLeave } from 'vue-router';
  import { onMounted, reactive, ref, computed, defineProps } from 'vue';

  const store = useStore();
  const { member, cart } = storeToRefs(store);
  const router = useRouter();
  const result = ref([]);
  let param = {};
  const mainSrc = ref([]);
  const checkedArr = ref([]);
  const btnDisabled = reactive({
    minus: [],
    plus: [],
  });

  onMounted(() => {
    getList();
  });

  onBeforeRouteLeave((to, from, next) => {
    allUpdate();

    mainSrc.value.forEach((item) => {
      URL.revokeObjectURL(item);
    });

    if (to.fullPath !== '/order/insert') {
      cart.value.checkArr = [];
    }
    next();
  });

  const priceFormat = computed(() => {
    return price => price > 0 ? price.toLocaleString() : 0
  });

  // const increase = computed(() => {
  //   return idx => result.value[idx].quantity < 10 ? result.value[idx].quantity++ : result.value[idx].quantity = 10;
  // });

  // const decrease = computed(() => {
  //   return idx => result.value[idx].quantity > 1 ? result.value[idx].quantity-- : result.value[idx].quantity = 1;
  // });

  const allCheck = computed({
    get() {
      return result.value?.length === checkedArr.value?.length;
    },
    set(value) {
      value ? checkedArr.value = result.value?.map(item => item.cno) : checkedArr.value = [];
    },
  });

  const subtotal = computed(() => {
    return result.value?.filter(item => checkedArr.value.includes(item.cno))
      .reduce((sum, item) => sum + (item.price * item.quantity), 0);
  });

  const charge = computed(() => {
    return subtotal.value >= 10000 || subtotal.value === 0 ? 0 : 100;
  });

  const increase = (item, i) => {
    item.amount--;
    item.quantity++;
    item.amount === 0 ? btnDisabled.plus[i] = true : btnDisabled.plus[i] = false
    item.quantity <= 1 ? btnDisabled.minus[i] = true : btnDisabled.minus[i] = false
  };

  const decrease = (item, i) => {
    item.amount++;
    item.quantity > 1 ? item.quantity-- : item.quantity = 1;
    item.amount === 0 ? btnDisabled.plus[i] = true : btnDisabled.plus[i] = false
    item.quantity <= 1 ? btnDisabled.minus[i] = true : btnDisabled.minus[i] = false
  };

  const getList = async() => {
    param = {};
    param.id = member.value.id;
    const res = await commonApi("/api/cart/list", 'GET', param);

    result.value = res.data;
    console.log(result.value);

    result.value?.forEach((item, idx) => {
      if (item.attachList.length > 0) {
        getImage(item.attachList[0], idx);
      }

      item.amount === 0 ? btnDisabled.plus.push(true): btnDisabled.plus.push(false)

      btnDisabled.minus.push(false);
    });

    checkedArr.value = result.value?.map(item => item.cno);
  };

  const getImage = async (item, i) => {
    try {
      param = {};
      param.filePath = item.filePath;
      param.fileName = item.fileName;
  
      const res = await commonApi("/api/file/getFile", "get", param);
      // console.log(res.data);
      
      const url = URL.createObjectURL(res.data); // url 생성
 
      // mainSrc.value.push(url);
      mainSrc.value[i] = url;

    } catch (e) { 
      console.log(e);
    }
  };

  const removeItem = async(item, idx) => {
    try {
      param = {};
      param.cno = item.cno;
      
      const res = await commonApi("/api/cart/delete", 'delete', param);
      
      if (res.status === 200) {
        alert("delete");
        
        const index = checkedArr.value.findIndex(i => i === item.cno);
        if (index > -1) {
          checkedArr.value.splice(index, 1);
        }

        result.value.splice(idx, 1);
        // result.value = result.value.filter(c => c.cno !== item.cno);

        store.getCartCount(member.value.id);
      }
      
    } catch (e) {
      console.log(e);
    }
  };

  const allUpdate = async() => {
    try {
      const res = await commonApi("/api/cart/updateList", "patch", result.value);
  
      // console.log(res);
      
    } catch (e) {
      console.log(e);
    }
  };

  const checkOut = () => {
    // 얕은 복사(스프레드 연산자)시 복사본 수정시 원본도 수정 되므로 깊은복사 
    let res = {"list" : JSON.parse(JSON.stringify(result.value))};
    res = res.list.filter(item => checkedArr.value.some(cno => item.cno === cno))
    .filter(item => delete item.attachList);
    
    res.charge = charge.value;
    res.totalPrice = subtotal.value;
    console.log(res);

    cart.value.checkArr = [];
    cart.value.checkArr.length = 0;
    cart.value.checkArr.push(checkedArr.value);

    router.push("/order/insert");
    
  };


  const applyCoupon = () => {

  }

</script>

<template>
  <v-container>
    <!-- Products List -->
    {{ checkedArr }}
    <v-table striped="even">
      <thead>
        <tr>
          <th class="text-center"><input type="checkbox" v-model="allCheck" /></th>
          <th class="text-center">Products</th>
          <th class="text-center">Name</th>
          <th class="text-center">Price</th>
          <th class="text-center">Quantity</th>
          <th>Total</th>
          <th>delete</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, i) in result" :key="item.cno">
          <td class="text-center">
            <!-- <v-checkbox
              v-model="checkedArr"
              :value="item.cno"
            ></v-checkbox> -->
            <input type="checkbox" v-model="checkedArr" :id="item.name" :value="item.cno">
            <label :for="item.name"></label>
          </td>
          <td width="150" class="text-center">
            <v-img v-if="item.attachList.length > 0" :src="mainSrc[i]" width="100" height="100" cover />
          </td>
          <td class="text-center">{{ item.name }}</td>
          <td class="text-center">{{ priceFormat(item.price) }}</td>
          <td class="text-center">
            <v-btn 
              @click="decrease(item, i)"
              :disabled="btnDisabled.minus[i]" 
              density="compact" 
              icon="mdi-minus" 
              variant="text" 
            ></v-btn>
            <span class="mx-2">{{ item.quantity }}</span>
            <v-btn 
              @click="increase(item, i)"
              :disabled="btnDisabled.plus[i]" 
              density="compact" 
              icon="mdi-plus" 
              variant="text"
            ></v-btn>
          </td>
          <td>{{ priceFormat(item.price * item.quantity) }}</td>
          <td>
            <v-btn color="error" icon="mdi-close" variant="text" @click="removeItem(item, i)" />
          </td>
        </tr>
      </tbody>
    </v-table>

    <!-- Coupon Section -->
    <v-row class="mt-6">
      <v-col cols="12" md="6">
        <!-- <h4>사용 가능 Point : 1000p</h4> -->
        <v-number-input 
          control-variant="hidden"
          prepend-icon="mdi-pencil"
          variant="outlined"
          density="compact"
          label="point"
          :min="0"
          :max="10000"
          required 
        ></v-number-input>
        <v-btn color="primary" class="mt-2" @click="applyCoupon">
          Apply Point
        </v-btn>
      </v-col>

      <!-- Cart Total -->
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title>Cart Total</v-card-title>
          <v-card-text>
            <div class="d-flex justify-space-between mb-2">
              <span>Subtotal:</span>
              <span>{{ priceFormat(subtotal) }}</span>
            </div>
            <div class="d-flex justify-space-between mb-2">
              <span>Shipping:</span>
              <div class="text-right">
                <div>Flat rate: {{ charge }}</div>
              </div>
            </div>
            <v-divider class="my-2"></v-divider>
            <div class="d-flex justify-space-between mb-4">
              <span class="text-h6">Total:</span>
              <span class="text-h6">{{ priceFormat(subtotal + charge) }}</span>
            </div>
            <v-btn
              @click="checkOut"
              :disabled="checkedArr.length > 0 ? false : true"
              color="success"
              block
              text="CHECKOUT"
            ></v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.v-table {
  background: transparent;
}
</style>