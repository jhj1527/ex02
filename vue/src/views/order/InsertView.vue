<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  
  const route = useRoute();
  const router = useRouter();
  const store = useStore();
  const { member, cart } = storeToRefs(store);

  const result = ref({});
  const input = reactive({
    id : "",
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
    list : {}
  });
  let param = {};

  onMounted(() => {
    input.id = member.value.id;
    getCheckList();
  });

  const priceFormat = computed(() => 
    (value) => value > 0 ? value.toLocaleString() : 0
  );
  // const priceFormat = computed(() => {
  //   return price ? price.toLocaleString() : 0;
  // });

  // const subTotal = computed(() => {
  //   return result.value?.reduce((sum, item) => sum + (item.price * item.amount), 0);
  // });

  // const charge = computed(() => {
  //   return subTotal >= 30000 ? 0 : 100;
  // });

  // const total = computed(() => {
  //   return subTotal + charge;
  // });

  const getCheckList = async () => {
    try {
      param = {};
      param.checkArr = cart.value.checkArr;
      const res = await commonApi("/api/cart/checkList", "get", param);
      result.value = res.data.filter(item => delete item.attachList);
      console.log(result.value);
  
      input.orderPrice = result.value?.reduce((sum, item) => sum + (item.price * item.amount), 0);
      input.charge = input.orderPrice >= 30000 ? 0 : 100;

    } catch (e) {
      console.log(e);
    }
  };

  const order = async () => {    
    try {
      input.list = result.value;
      let res = await commonApi("/api/order/insert", "post", input);
  
      if (res.status === 200 || res.status === 201) {
        console.log(res.data);
  
        param = {};
        param.id = input.id;
        res = await commonApi("/api/cart/getCount", "get", param);
        cart.value.count = res.data;
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
      order();
  
      // // 포트원 고객사 식별코드
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
      // }, async function(res) {
      //   if (res.success) {
      //     console.log(res);
      //     input.imp_uid = res.imp_uid;
      //     order();
  
      //   } else {
      //     alert("결제 오류");
      //     console.log(res);
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
  <div class="container my-5">
    <div class="row">
      <!-- Billing Details -->
      <div class="col-lg-8">
        <h4 class="fw-bold mb-4">Check out</h4>
        <div class="mb-3">
          <label class="form-label">id<span class="text-danger">*</span></label>
          <input type="text" v-model="input.id" class="form-control" />
        </div>
        <div class="mb-3">
          <label class="form-label">Address<span class="text-danger">*</span></label>
          <div class="d-flex mb-2">
            <input type="text" v-model="input.postCode" class="form-control me-2" style="max-width: 150px;" placeholder="우편번호" />
            <button type="button" @click="PostCodeApi" class="btn btn-outline-secondary" style="min-width: 120px;">우편번호 찾기</button>
          </div>
          <input type="text" v-model="input.address1" class="form-control mb-2" placeholder="도로명주소" />
          <div class="row mb-3">
            <div class="col-md-6">
              <input type="text" v-model="input.address2" class="form-control" placeholder="상세주소" />
            </div>
            <div class="col-md-6">
              <input type="email" v-model="input.address3" class="form-control" placeholder="참고항목" />
            </div>
          </div>
        </div>
        <div class="row mb-3">
          <div class="col-md-6">
            <label class="form-label">Phone<span class="text-danger">*</span></label>
            <input type="text" v-model="input.phone" class="form-control" />
          </div>
          <div class="col-md-6">
            <label class="form-label">Email<span class="text-danger">*</span></label>
            <input type="email" v-model="input.email" class="form-control" />
          </div>
        </div>
      </div>
      <!-- Order Summary -->
      <div class="col-lg-4">
        <div class="bg-light p-4 rounded">
          <h5 class="fw-bold mb-4">Your Order</h5>
          <div class="mb-3">
            <div class="d-flex justify-content-between fw-bold mb-2">
              <span style="flex:2;">Products</span>
              <span style="flex:1; text-align:center;">Qty</span>
              <span style="flex:1; text-align:right;">Total</span>
            </div>
            <div v-for="(item, i) in result" :key="item.cno" class="d-flex justify-content-between align-items-center">
              <span v-html="item.name" style="flex:2;"></span>
              <span v-html="item.amount" style="flex:1; text-align:center;"></span>
              <span v-html="priceFormat(item.price)" class="fw-bold" style="flex:1; text-align:right;"></span>
            </div>
            <hr />
            <div class="d-flex justify-content-between fw-bold">
              <span>Subtotal</span>
              <span>{{ priceFormat(input.orderPrice) }}</span>
            </div>
            <div class="d-flex justify-content-between fw-bold">
              <span>charge</span>
              <span>{{ priceFormat(input.charge) }}</span>
            </div>
            <div class="d-flex justify-content-between fw-bold">
              <span>Total</span>
              <span class="text-danger">{{ priceFormat(input.orderPrice + input.charge) }}</span>
            </div>
          </div>
          <!-- <div class="form-check mb-2">
            <input class="form-check-input" type="checkbox" id="checkPayment" />
            <label class="form-check-label" for="checkPayment">
              cash
            </label>
          </div>
          <div class="form-check mb-4">
            <input class="form-check-input" type="checkbox" id="paypal" />
            <label class="form-check-label" for="paypal">
              card
            </label>
          </div> -->
          <button type="button" @click="importApi" class="btn btn-success w-100 fw-bold py-2" style="font-size: 1.1rem;">
            PLACE ORDER
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Optional: adjust spacing and font to match the image more closely */
.form-label {
  font-weight: 500;
}
</style>