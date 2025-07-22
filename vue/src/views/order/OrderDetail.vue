<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import UpdateView from './UpdateView.vue';

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  const props = defineProps({
    orderId: [String, Number],
  });
  const result = ref("");
  let param = {};
  const total = reactive({
    totalRealPrice : 0,
    totalDiscountPrice : 0,
    point : 0,
  });

  const mainSrc = ref([]);
  const itemRefs = ref({});
  const modal = reactive({
    isModal : false,
    dto : {},
  })

  onMounted(() => {
    getDetail();
  });

  const priceFormat = computed(() => 
    (value) => value > 0 ? value.toLocaleString() : 0
  );

  const realPrice = computed(() => 
    (item) => item.discount > 0 ? item.price * 100 / (100 - item.discount) : item.price
  );

  const discountPrice = computed(() => 
    (item) => item.discount > 0 ? (item.price * 100 / (100 - item.discount) - item.price) * item.amount : 0
  );

  const getDetail = async () => {
    try {
      const res = await commonApi(`/api/order/detailList/${props.orderId}`, "get");
      result.value = res.data;
  
      // if (result.value.state === 1) {
      //   result.value.state = "배송준비";
  
      // } else if (result.value.state === 2) {
      //   result.value.state = "배송중";
  
      // } else if (result.value.state === 3) {
      //   result.value.state = "배송완료";
      // }
  
      // console.log(result.value);
  
      total.totalRealPrice = result.value.list.reduce((sum, item) => {
        if (item.discount > 0) {
          sum += item.price * 100 / (100 - item.discount) * item.amount;
  
        } else {
          sum += (item.price * item.amount);
        }
  
        return sum;
      }, 0);
  
      total.totalDiscountPrice = total.totalRealPrice - result.value.orderPrice;
      total.point = result.value.orderPrice * 0.1;
  
      result.value.list.forEach((item, i) => {
        if (item.attachDto !== null) {
          getImage(item.attachDto, i);
        }
      });

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
      mainSrc.value.push(url);
  
      itemRefs.value[i].src = url;
      
    } catch (e) { 
      console.log(e);
    }
  };

  const setItemRef = async (el, idx) => {
    if (el) {
      itemRefs.value[idx] = el;
    }
  };

  const close = () => {
    modal.isModal = !modal.isModal;
  };

  const update = () => {
    modal.isModal = !modal.isModal;
    getDetail();
  };

  const popup = () => {
    // 배송지 변경 모달창에 넘겨주기 위한 객체
    modal.dto = JSON.parse(JSON.stringify(result.value));
    modal.isModal = true;
  };

</script>

<template>
  <div class="container my-5">
    <!-- 주문 상품 -->
    <h5 class="fw-bold mb-3">주문 상품</h5>
    <table class="table align-middle">
      <thead>
        <tr>
          <th style="width: 120px;"></th>
          <th>상품명</th>
          <th>수량</th>
          <th>상품금액</th>
          <th>할인금액</th>
          <th>총금액</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(item, idx) in result.list" :key="item.oino" >
          <td>
            <!-- :src="`http://localhost:8081/api/file/getFile?filePath=${item.attachDto.filePath}&fileName=${item.attachDto.fileName}`" -->
            <img 
              v-if="item.attachDto !== null"
              :src="mainSrc"
              :ref="el => setItemRef(el, idx)"
              class="img-thumbnail" 
              alt="">
            <img v-else src="../../assets/image/golden-retriever-puppy-amber-9661916_640.jpg" class="img-thumbnail" alt="상품1">
          </td>
          <td>{{ item.name }}</td>
          <td>{{ item.amount }}</td>
          <td>{{ priceFormat(realPrice(item)) }}</td>
          <td>{{ priceFormat(discountPrice(item))}}</td>
          <td>{{ priceFormat(item.price * item.amount) }}</td>
        </tr>
      </tbody>
    </table>

    <div class="row mt-4">
      <!-- 주문자 정보 -->
      <div class="col-md-6">
        <h6 class="fw-bold mb-3">주문자 정보</h6>
        <table class="table table-border">
          <tbody>
            <tr>
              <td class="text-secondary">주문번호</td>
              <td class="text-end">{{ result.orderId }}</td>
            </tr>
            <tr>
              <td class="text-secondary">주문일</td>
              <td class="text-end">{{ result.regDate }}</td>
            </tr>
            <tr>
              <td class="text-secondary">이름</td>
              <td class="text-end">{{ result.id }}</td>
            </tr>
            <tr>
              <td class="text-secondary">휴대폰번호</td>
              <td class="text-end">{{ result.phone }}</td>
            </tr>
            <tr>
              <td class="text-secondary">이메일</td>
              <td class="text-end">{{ result.email }}</td>
            </tr>
          </tbody>
        </table>
      </div>
      <!-- 결제 정보 -->
      <div class="col-md-6">
        <h6 class="fw-bold mb-3">결제 정보</h6>
        <table class="table table-border">
          <tbody>
            <tr>
              <td class="text-secondary">상품금액</td>
              <td class="text-end">{{ priceFormat(total.totalRealPrice) }}</td>
            </tr>
            <tr>
              <td class="text-secondary">할인 금액</td>
              <td class="text-end text-danger">{{ priceFormat(total.totalDiscountPrice) }}</td>
            </tr>
            <tr>
              <td class="text-secondary">적립예정 포인트</td>
              <td class="text-end">{{ total.point }}P</td>
            </tr>
            <tr>
              <td class="text-secondary">배송비</td>
              <td class="text-end">{{ priceFormat(result.charge) }}</td>
            </tr>
            <tr>
              <td class="fw-bold">총 결제 금액</td>
              <td class="text-end fw-bold text-danger">{{ priceFormat(result.orderPrice + result.charge) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="row mt-4">
      <!-- 배송지 정보 -->
      <div class="col-md-6">
        <h6 class="fw-bold mb-3">배송지 정보</h6>
        <div class="border rounded p-3">
          <div>{{ result.id }}</div>
          <div>{{ result.phone }}</div>
          <div>{{ result.postCode }} {{ result.address1 }} {{ result.address2 }} {{ result.address3 }}</div>
          <button @click="popup" class="btn btn-danger btn-sm mt-2">배송지 변경</button>
        </div>
      </div>
    </div>

    <div class="d-flex justify-content-center gap-2 mt-4">
      <button class="btn btn-outline-secondary" @click="router.push('/order/list')">주문목록 보기</button>
      <button class="btn btn-danger" @click="router.push('/item/list')">계속 쇼핑하기</button>
    </div>
  </div>

  <UpdateView :isModal="modal.isModal" :dto="modal.dto" @close="close()" @update="update()"/>
</template>