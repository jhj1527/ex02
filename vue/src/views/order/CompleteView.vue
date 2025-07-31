<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref, defineProps } from 'vue';
  import { useRouter } from 'vue-router';
  import dayjs from 'dayjs';
  
  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  const props = defineProps({
    orderId: [String, Number],
  });
  const today = dayjs().format("YYYY.MM.DD hh:mm:ss");
  let param = {};

  onMounted(() => {
    console.log(props.orderId);
  });

  const getOrder = async () => {
    try {
      // param.id = member.value.id;
      // param.orderId = props.orderId;
      // const res = commonApi("/api/order/get", "get", param);
      // console.log(res);
      
    } catch (e) {
      console.error(e);
    }
  };
</script>

<template>
  <v-container class="h-screen d-flex align-center">
    <v-row justify="center">
      <v-col cols="12" md="6">
        <v-card class="pa-6 text-center" elevation="2">
          <!-- Order complete text -->
          <v-card-title class="text-h5 font-weight-bold mb-3">
            주문이 완료되었습니다
          </v-card-title>
          
          <v-card-text class="mb-4">
            <span class="font-weight-bold">{{ today }}</span>
            <span class="text-grey-darken-1 ms-1">
              주문하신 상품의<br>주문번호는 {{ props.orderId }} 입니다.
            </span>
          </v-card-text>

          <!-- Buttons -->
          <v-card-actions class="justify-center gap-3">
            <v-btn
              variant="outlined"
              color="grey-darken-1"
              to="/order/list"
            >
              주문내역 보기
            </v-btn>
            <v-btn
              color="error"
              to="/item/list"
            >
              계속 쇼핑하기
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>

</style>