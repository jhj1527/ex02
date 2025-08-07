<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import UpdateView from './UpdateView.vue';
  import dayjs from 'dayjs';

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  const props = defineProps({
    orderId: [String, Number],
  });
  const result = ref("");
  let param = {};
  const mainSrc = ref([]);
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
    (item) => item.discount > 0 ? (item.price * 100 / (100 - item.discount) - item.price) * item.quantity : 0
  );

  const stateFormat = computed(() => (state) => {
    if (state === 1) {
      state = "배송준비";

    } else if (state === 2) {
      state = "배송중";

    } else if (state === 3) {
      state = "배송완료";

    } else if (state === 4) {
      state = "주문취소";
    }
    return state;
  });

  const totalRealPrice = computed(() => {
    return result.value.list?.reduce((sum, item) => {
      if (item.discount > 0) {
        sum += item.price * 100 / (100 - item.discount) * item.quantity;

      } else {
        sum += (item.price * item.quantity);
      }
      return sum;
    }, 0);
  });

  const totalDiscountPrice = computed(() => {
    return totalRealPrice.value - result.value.list?.reduce((sum, item) => sum += (item.price * item.quantity), 0);
  });

  const point = computed(() => {
    return result.value.orderPrice * 0.1
  });

  const getDetail = async () => {
    try {
      const res = await commonApi(`/api/order/detailList/${props.orderId}`, "get");
      result.value = res.data;
  
      console.log(result.value);
  
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
      // mainSrc.value.push(url);
      mainSrc.value[i] = url;
    } catch (e) { 
      console.log(e);
    }
  };

  const cancel = async (item) => {
    try {
      param = {};
      
      const res = await commonApi("/api/order/cancel", "delete", item);
      if (res.status === 200) {
        alert("cancel");
        getDetail();
      }
      // param.
      // const res = await commonApi("/api/payment/cancel", "post", item);
      // if (res.data.response !== null) {
      //   param = {};
      //   param.orderId = item.orderId;
      //   const res = await commonApi("/api/order/delete", "delete", param);
        
      //   if (res.status === 200) {
      //     alert("cancel");
      //     result.value = result.value.map(item => item.state = '주문취소');
      //     // result.value = result.value.filter(i => i.orderId !== item.orderId);
      //   }
  
      // } else {
      //   alert("이미 취소된 내역");
      // }
      
    } catch (e) {
      console.error(e);
    }
  };

  const insertReview = async (item) => {
    try {
      
    } catch (e) {
      console.error(e);
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

  const getStatusColor = (status) => {
    const statusColors = {
      '배송준비': 'secondary',
      '배송중': 'primary',
      '배송완료': 'success',
      '주문취소': 'error'
    }
    return statusColors[status] || 'grey'
  }

</script>

<template>
  <v-container class="my-5">
    <!-- 주문 상품 -->
    <v-card>
      <v-card-title class="text-h5 font-weight-bold">주문 상품</v-card-title>
      <v-table>
        <thead>
          <tr>
            <th></th>
            <th>상품명</th>
            <th>수량</th>
            <th>상품금액</th>
            <th>할인금액</th>
            <th>총금액</th>
            <th>진행상태</th>
            <th>접수</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(item, i) in result.list" :key="item.oino">
            <td width="120">
              <v-img
                v-if="item.attachDto !== null"
                :src="mainSrc[i]"
                :max-width="120"
                :max-height="120"
                cover
              ></v-img>
              <v-img v-else src="../../assets/image/golden-retriever-puppy-amber-9661916_640.jpg" max-width="150" cover></v-img>
            </td>
            <td>{{ item.name }}</td>
            <td>{{ item.quantity }}</td>
            <td>{{ priceFormat(realPrice(item)) }}</td>
            <td>{{ priceFormat(discountPrice(item))}}</td>
            <td>{{ priceFormat(item.price * item.quantity) }}</td>
            <td>
              <v-chip
                :color="getStatusColor(stateFormat(item.state))"
                variant="outlined"
                class="font-weight-bold"
              >
                {{ stateFormat(item.state) }}
              </v-chip>
            </td>
            <td>
              <v-btn
                v-if="item.state === 1 || item.state === 2"
                color="error"
                variant="outlined"
                @click="cancel(item)"
                text="주문취소"
                ></v-btn>
              <v-btn
                v-else-if="item.state === 3"
                color="warning"
                variant="outlined"
                @click="insertReview(item)"
                text="리뷰작성"
              ></v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>

      <v-row class="mt-4">
        <!-- 주문자 정보 -->
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="text-h6 font-weight-bold">주문자 정보</v-card-title>
            <v-table density="compact">
              <tbody>
                <tr>
                  <td class="text-grey">주문번호</td>
                  <td class="text-right">{{ result.orderId }}</td>
                </tr>
                <tr>
                  <td class="text-grey">주문일</td>
                  <td class="text-right">{{ dayjs(result.regDate).format('YYYY-MM-DD HH:mm:ss') }}</td>
                </tr>
                <tr>
                  <td class="text-grey">이름</td>
                  <td class="text-right">{{ result.id }}</td>
                </tr>
                <tr>
                  <td class="text-grey">휴대폰번호</td>
                  <td class="text-right">{{ result.phone }}</td>
                </tr>
                <tr>
                  <td class="text-grey">이메일</td>
                  <td class="text-right">{{ result.email }}</td>
                </tr>
              </tbody>
            </v-table>
          </v-card>
        </v-col>

        <!-- 결제 정보 -->
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="text-h6 font-weight-bold">결제 정보</v-card-title>
            <v-table density="compact">
              <tbody>
                <tr>
                  <td class="text-grey">상품금액</td>
                  <td class="text-right">{{ priceFormat(totalRealPrice) }}</td>
                </tr>
                <tr>
                  <td class="text-grey">할인 금액</td>
                  <td class="text-right">{{ priceFormat(totalDiscountPrice) }}</td>
                </tr>
                <tr>
                  <td class="text-grey">적립예정 포인트</td>
                  <td class="text-right">{{ point }}P</td>
                </tr>
                <tr>
                  <td class="text-grey">배송비</td>
                  <td class="text-right">{{ priceFormat(result.charge) }}</td>
                </tr>
                <tr v-if="result.cancelPrice > 0">
                  <td class="text-grey">취소금액</td>
                  <td class="text-right text-red">{{ priceFormat(result.cancelPrice) }}</td>
                </tr>
                <tr v-if="result.orderPrice + result.charge > 0">
                  <td class="font-weight-bold">총 결제 금액</td>
                  <td class="text-right font-weight-bold text-red">{{ priceFormat(result.orderPrice + result.charge) }}</td>
                </tr>
              </tbody>
            </v-table>
          </v-card>
        </v-col>
      </v-row>

      <v-row class="mt-4">
        <!-- 배송지 정보 -->
        <v-col cols="12" md="6">
          <v-card>
            <v-card-title class="text-h6 font-weight-bold">배송지 정보</v-card-title>
            <v-card-text>
              <div>{{ result.id }}</div>
              <div>{{ result.phone }}</div>
              <div>{{ result.postCode }} {{ result.address1 }} {{ result.address2 }} {{ result.address3 }}</div>
              <v-btn color="warning" variant="outlined" class="mt-2" @click="popup">배송지 변경</v-btn>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <v-row class="mb-4 mt-4 justify-center">
        <v-btn variant="outlined" class="mx-2" @click="router.push('/order/list')">주문목록 보기</v-btn>
        <v-btn variant="outlined" color="error" @click="router.push('/item/list')">계속 쇼핑하기</v-btn>
      </v-row>
    </v-card>

    <UpdateView :isModal="modal.isModal" :dto="modal.dto" @close="close()" @update="update()"/>
  </v-container>
</template>