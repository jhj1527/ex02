<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import dayjs from 'dayjs';
  
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  const router = useRouter();
  const store = useStore();
  const { member, cart } = storeToRefs(store);
  const day = ref("");

  const result = ref({});
  let param = {};

  onMounted(() => {
    orderList();
  });

  const dayFormat = computed(() => 
    (value) => dayjs(value).format("YYYY.MM.DD hh:mm:ss")
  );

  const priceFormat = computed(() => 
    (value) => value > 0 ? value.toLocaleString() : 0
  );

  const orderList = async () => {
    param = {};
    param.id = member.value.id;
    const res = await commonApi("/api/order/list", "get", param);
    result.value = res.data.map(item => {
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
</script>

<template>
  <div class="bg-light min-vh-100 py-4">
    <div class="container">
      <!-- Header -->
      <div class="d-flex align-items-center mb-4">
        <h1 class="fw-bold mb-0 me-2">주문내역</h1>
        <span class="fs-3 text-secondary"></span>
      </div>

      <!-- Card -->
      <div class="bg-white rounded shadow-sm p-4">
        <!-- Toolbar -->
        <div class="d-flex align-items-center mb-3 flex-wrap gap-2">
          <div class="dropdown me-2">
            <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
              <i class="bi bi-info-circle me-1"></i> All items <span class="text-secondary"> (138)</span>
            </button>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#">All items</a></li>
              <li><a class="dropdown-item" href="#">Paid</a></li>
              <li><a class="dropdown-item" href="#">Unfulfilled</a></li>
            </ul>
          </div>
          <div class="dropdown me-2">
            <button class="btn btn-link text-decoration-none dropdown-toggle" type="button" data-bs-toggle="dropdown">
              Manage View
            </button>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#">Default</a></li>
              <li><a class="dropdown-item" href="#">Custom</a></li>
            </ul>
          </div>
          <div class="ms-auto d-flex align-items-center gap-2">
            <button class="btn btn-outline-primary">
              <i class="bi bi-funnel"></i> Filter
            </button>
            <button class="btn btn-outline-secondary">
              <i class="bi bi-sliders"></i>
            </button>
            <div class="input-group ms-2" style="width: 200px;">
              <span class="input-group-text bg-white border-end-0"><i class="bi bi-search"></i></span>
              <input type="text" class="form-control border-start-0" placeholder="Search...">
            </div>
          </div>
        </div>

        <!-- Table -->
        <div class="table-responsive">
          <table class="table align-middle">
            <thead class="table-light">
              <tr>
                <th scope="col" class="text-primary">OrderId</th>
                <th scope="col">OrderDate</th>
                <th scope="col">Customer</th>
                <th scope="col">state</th>
                <th scope="col">Total</th>
                <th scope="col"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in result" :key="item.orderId">
                <td>
                  <RouterLink :to="`/order/detail/${item.orderId}`" style="color: black; text-decoration: none;" >{{ item.orderId }}</RouterLink>
                </td>
                <td> {{ dayFormat(item.regDate) }}</td>
                <td>{{ item.id }}</td>
                <td>
                  <span class="badge bg-success bg-opacity-25 text-success fw-bold px-3 py-2" style="font-size: 1em;">
                    {{ item.state }}
                  </span>
                </td>
                <td class="fw-bold">{{ priceFormat(item.orderPrice + item.charge) }}</td>
                <td>
                    <button @click="cancel(item)" class="btn btn-outline-primary fw-bold px-3 py-2" style="font-size: 1em;">주문취소</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>
  
<style scoped>
.table th, .table td {
  vertical-align: middle;
}
.table thead th {
  background-color: #f6f8fa;
  font-weight: 500;
}
.bg-light {
  background-color: #f6f8fa !important;
}
</style>