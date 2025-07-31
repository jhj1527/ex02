<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import dayjs from 'dayjs';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref } from 'vue';
  import { useRouter } from 'vue-router';

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  const result = ref([]);
  let param = {};
  const loading = ref(false)
  const dateRange = ref([])
  const orders = ref([])

  const headers = [
    { title: '주문번호', align: 'start', key: 'orderId' },
    { title: '주문일자', key: 'orderDate' },
    { title: '주문자명', key: 'id' },
    { title: '주문금액', key: 'totalPrice' },
    { title: '주문상태', key: 'status' },
    { title: '관리', key: 'actions' }
  ];

  onMounted(() => {
    orderList()
  });

  const dayFormat = computed(() => 
    (value) => dayjs(value).format("YYYY-MM-DD hh:mm:ss")
  );

  const priceFormat = computed(() => 
    (value) => value > 0 ? value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',') : 0
  );

  const orderList = async () => {
    try {
      loading.value = true;
      param = {};
      param.id = member.value.id;
      const res = await commonApi("/api/order/list", "get", param);
      result.value = res?.data.map(item => {
        if (item.state === 1) {
          item.state = "배송준비";

        } else if (item.state === 2) {
          item.state = "배송중";

        } else if (item.state === 3) {
          item.state = "배송완료";
        }
        return item;
      });
      console.log(result.value);
    } catch (e) {
      console.log(e);

    } finally {
      loading.value = false
    }  
  };

  const cancel = async (item) => {
    try {
      param = {};
      param.imp_uid = item.imp_uid;
      param.orderPrice = item.orderPrice;
      param.charge = item.charge;
      const res = await commonApi("/api/payment/cancel", "post", param);
  
      console.log(res);
  
      if (res.data.response !== null) {
        param = {};
        param.orderId = item.orderId;
        const res = await commonApi("/api/order/delete", "delete", param);
        
        if (res.status === 200) {
          alert("cancel");
          result.value = result.value.filter(i => i.orderId !== item.orderId);
        }
  
      } else {
        alert("이미 취소된 내역");
      }
      
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

  const getStatusColor = (status) => {
    const statusColors = {
      '배송준비': 'secondary',
      '배송중': 'primary',
      '배송완료': 'success',
      '취소': 'error'
    }
    return statusColors[status] || 'grey'
  }

  const viewOrderDetail = (item) => {
    router.push(`/order/${item.orderId}`)
  }

</script>

<template>
  <v-container>
    <!-- Header -->
    <v-row class="mb-4">
      <v-col>
        <h1 class="text-h4 font-weight-bold">주문내역</h1>
      </v-col>
    </v-row>

    <!-- Card -->
    <v-card>
      <v-card-text>
        <!-- Table -->
        <v-data-table
          :headers="headers"
          :items="result"
          :loading="loading"
        >
          <template v-slot:item.orderId="{ item }">
            <v-btn
              variant="text"
              :to="`/order/detail/${item.orderId}`"
            >
              {{ item.orderId }}
            </v-btn>
          </template>

          <template v-slot:item.orderDate="{ item }">
            {{ dayFormat(item.regDate) }}
          </template>

          <template v-slot:item.id="{ item }">
            {{ item.id }}
          </template>

          <template v-slot:item.status="{ item }">
            <v-chip
              :color="getStatusColor(item.state)"
              variant="outlined"
              class="font-weight-bold"
            >
              {{ item.state }}
            </v-chip>
          </template>

          <template v-slot:item.totalPrice="{ item }">
            {{ priceFormat(item.orderPrice + item.charge) }}
          </template>

          <template v-slot:item.actions="{ item }">
            <v-btn
              v-if="item.state === '배송준비' || item.state === '배송중'"
              color="error"
              variant="outlined"
              @click="cancel(item)"
              text="주문취소"
            ></v-btn>
            <v-btn
              v-else
              color="warning"
              variant="outlined"
              @click="insertReview(item)"
              text="리뷰"
            ></v-btn>
          </template>
        </v-data-table>
      </v-card-text>
    </v-card>
  </v-container>
</template>

<style scoped>

</style>