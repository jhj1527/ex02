<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref, defineProps } from 'vue';
  import { useRoute, useRouter } from 'vue-router';
  
  const route = useRoute();
  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  const props = defineProps({
    orderId: [String, Number],
  });
  let orderDate = ref("");

  onMounted(() => {
    console.log(props.orderId);
    getDate();
  });

  const getDate = async () => {
    let today = new Date();
    let year = today.getFullYear();
    let month = today.getMonth() + 1;
    let day = today.getDate();
    
    if (month < 10) {
      month = "0" + month;
    }

    orderDate.value = year + "." + month + "." + day;
    // param.id = member.value.id;
    // param.orderId = props.orderId;
    // const res = commonApi("/api/order/get", "get", param);
    // console.log(res);
  };
</script>

<template>
  <div class="d-flex justify-content-center align-items-center" style="min-height: 80vh;">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6 text-center bg-white rounded shadow-sm py-5">
          <!-- Order complete text -->
          <h5 class="fw-bold mb-3">주문이 완료되었습니다</h5>
          <div class="mb-4">
            <span class="fw-bold">{{ orderDate }}</span>
            <span class="text-muted ms-1">주문하신 상품의<br>주문번호는 {{ props.orderId }} 입니다.</span>
          </div>
          <!-- Buttons -->
          <div class="d-flex justify-content-center gap-3">
            <RouterLink to="/order/list" style="color: black; text-decoration: none;">
              <button type="button" class="btn btn-outline-secondary px-4">주문내역 보기</button>
            </RouterLink>
            <RouterLink to="/item/list" style="color: black; text-decoration: none;">
              <button type="button" class="btn btn-danger px-4">계속 쇼핑하기</button>
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
body {
  background-color: #f8f9fa;
}
</style>