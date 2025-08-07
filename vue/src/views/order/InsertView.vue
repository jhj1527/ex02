<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref } from 'vue';
  import { useRouter, onBeforeRouteLeave } from 'vue-router';
  
  const router = useRouter();
  const store = useStore();
  const { member, cart, itemIno } = storeToRefs(store);

  const result = ref([]);
  const input = reactive({
    id : member.value.id || "",
    postCode : "",
    address1 : "",
    address2 : "",
    address3 : "",
    orderPrice : 0,
    charge : 0,
    phone : "",
    email : "",
    imp_uid : "",
    orderId : "",
    list : [],
  });
  let param = {};

  onMounted(() => {
    if (itemIno.value !== null) {
      getItem();

    } else if (cart.value.checkArr.length > 0) {
      getCheckList();
    }
  });

  onBeforeRouteLeave((to, from, next) => {
    cart.value.checkArr = [];
    itemIno.value = null;
    next();
  });

  const priceFormat = computed(() => 
    (value) => value > 0 ? value.toLocaleString() : 0
  );
  
  // const subTotal = computed(() => {
  //   return result.value?.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  // });

  // const charge = computed(() => {
  //   return subTotal >= 30000 ? 0 : 100;
  // });

  // const total = computed(() => {
  //   return subTotal + charge;
  // });

  const getItem = async () => {
    try {
      console.log(itemIno.value);
      const res = await commonApi("/api/item/" + itemIno.value, "get");
      // result.value = res.data;

      delete res.data.attachList;
      res.data.amount = 0;
      res.data.id = input.id;
      res.data.quantity = 1;
      res.data.price = res.data.discount > 0 ? Math.round(res.data.price * (1 - res.data.discount / 100)) : res.data.price;
      
      result.value.push(res.data);
      // console.log(result.value);
  
      input.orderPrice = result.value[0].price * result.value[0].quantity;
      input.charge = input.orderPrice >= 10000 ? 0 : 100;

    } catch (e) {
      console.log(e);
    }
  };

  const getCheckList = async () => {
    try {
      param = {};
      param.checkArr = cart.value.checkArr;
      const res = await commonApi("/api/cart/checkList", "get", param);
      result.value = res.data.filter(item => delete item.attachList);
      console.log(result.value);
  
      input.orderPrice = result.value?.reduce((sum, item) => sum + (item.price * item.quantity), 0);
      input.charge = input.orderPrice >= 30000 ? 0 : 100;

    } catch (e) {
      console.log(e);
    }
  };

  const order = async () => {    
    try {
      const res = await commonApi("/api/order/insert", "post", input);
  
      if (res.status === 200 || res.status === 201) {
        console.log(res.data);
  
        store.getCartCount(input.id);
        cart.value.checkArr = [];
        cart.value.checkArr.length = 0;
  
        alert('결제 완료');
  
        router.push("/order/complete/" + input.orderId);
  
      } else {
        alert("주문 오류");
      }

    } catch (e) {
      console.log(e);
    } 
  };

  const importApi = async () => {
    try {
      const res = await commonApi("/api/order/getOrderId", "get");
      console.log(res.data);
      input.imp_uid = "123456789";
      input.orderId = res.data;
      input.list = result.value;
      console.log(input);
      order();
  
      // 포트원 고객사 식별코드
      // IMP.init("imp48621712");
  
      // IMP.request_pay({
      //   pg : "html5_inicis", // 실제 계약 후에는 실제 상점아이디로 변경
      //   pay_method : "card", // 'card'만 지원됩니다.
      //   merchant_uid: input.orderId, // 상점에서 관리하는 주문 번호
      //   name : "test",
      //   amount : input.orderPrice + input.charge, // 결제창에 표시될 금액. 실제 승인이 이루어지지는 않습니다. (모바일에서는 가격이 표시되지 않음)
      //   // customer_uid : 'your-customer-unique-id', // 필수 입력.
      //   buyer_email : input.email,
      //   buyer_name : input.id,
      //   buyer_tel : input.phone,
      //   // m_redirect_url : '{모바일에서 결제 완료 후 리디렉션 될 URL}' // 예: https://www.my-service.com/payments/complete/mobile
      // }, async function(response) {
      //   if (response.success) {
      //     console.log(response);
      //     input.imp_uid = response.imp_uid;
      //     order();
  
      //   } else {
      //     alert("결제 오류");
      //     console.log(response);
      //     input.imp_uid = "";
      //   }
      // });
      
    } catch (e) {
      console.log(e);
    }
  }

  const PostCodeApi = async () => {
    store.PostCodeApi(input);
  };
  
</script>

<template>
  <v-container>
    <v-row>
      <v-col cols="12" md="8">
        <v-card class="pa-4 mb-4">
          <h2 class="text-h5 mb-4">order</h2>
          <v-text-field
            v-model="input.id"
            label="id*"
            required
            readonly
          ></v-text-field>
          <v-row>
            <v-col cols="3">
              <v-text-field
              v-model="input.postCode"
              label="우편번호"
              readonly
              ></v-text-field>
            </v-col>
            <v-col cols="3">
              <v-btn block
                @click="PostCodeApi" 
                size="large"
                text="우편번호 찾기"
                ></v-btn>
            </v-col>
          </v-row>
          <v-text-field
            v-model="input.address1"
            label="주소"
            readonly
          ></v-text-field>
          <v-row>
            <v-col cols="6">
              <v-text-field
                v-model="input.address2"
                label="상세주소"
              ></v-text-field>
            </v-col>
            <v-col cols="6">
              <v-text-field
                v-model="input.address3"
                label="참고항목"
                readonly
              ></v-text-field>
            </v-col>
          </v-row>
          <v-text-field
            v-model="input.phone"
            label="Mobile*"
            required
          ></v-text-field>
          <v-text-field
            v-model="input.email"
            label="Email Address*"
            required
          ></v-text-field>
        </v-card>
      </v-col>

      <!-- Order Summary -->
      <v-col cols="12" md="4">
        <v-card class="pa-4">
          <h2 class="text-h5 mb-4">Order Summary</h2>
            <v-list>
              <!-- Column Headers -->
              <v-list-item>
                <v-row align="center">
                <v-col cols="4">
                  <strong>Name</strong>
                </v-col>
                <v-col cols="4" class="text-center">
                  <strong>Quantity</strong>
                </v-col>
                <v-col cols="4" class="text-right">
                  <strong>Price</strong>
                </v-col>
                </v-row>
              </v-list-item>

              <v-divider></v-divider>

              <!-- List Items -->
              <v-list-item v-for="item in result" :key="item.ino">
                <v-row align="center">
                <v-col cols="4">
                  <v-list-item-title>{{ item.name }}</v-list-item-title>
                </v-col>
                <v-col cols="4" class="text-center">
                  <v-list-item-title>{{ item.quantity }}</v-list-item-title>
                </v-col>
                <v-col cols="4" class="text-right">
                  <v-list-item-title>{{ priceFormat(item.price * item.quantity) }}</v-list-item-title>
                </v-col>
                </v-row>
              </v-list-item>
            </v-list>

          <v-divider class="my-4"></v-divider>
          
          <div class="d-flex justify-space-between mb-2">
            <span>Subtotal</span>
            <span>{{ priceFormat(input.orderPrice) }}</span>
          </div>
          <div class="d-flex justify-space-between mb-4">
            <span>Charge</span>
            <span>{{ priceFormat(input.charge) }}</span>
          </div>
          <div class="d-flex justify-space-between text-h6">
            <strong>Total</strong>
            <strong>{{ priceFormat(input.orderPrice + input.charge) }}</strong>
          </div>

          <v-btn
            color="primary"
            block
            class="mt-4"
            size="large"
            @click="importApi"
          >
            Place Order
          </v-btn>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>

</style>