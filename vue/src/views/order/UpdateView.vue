<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { defineProps, defineEmits, reactive, ref } from 'vue';
  
  const store = useStore();
  const { member } = storeToRefs(store);
  const emits = defineEmits(["close", "update"]);
  const props = defineProps({
    dto: {
      type: Object,
      default: () => ({}),
    },
    isModal: {
      type: Boolean,
      default: false,
    }
  });

  const input = reactive({

  });

  const PostCodeApi = () => {
    store.PostCodeApi(props.dto);
  };

  const update = async () => {
    try {
      console.log(props.dto);
      // let param = {};
      
      const res = await commonApi("/api/order/update", "patch", props.dto);
      
      if (res.status === 200) {
        alert("update");
        emits("update");

      } else {
        alert("error");
      }
      
    } catch (e) {
      console.error(e);
    }
  };

</script>

<template>
  <div class="order-update-popup" v-if="isModal">
    <div class="card">
      <div class="card-header">배송지 변경</div>
      <div class="card-body">
        <div class="mb-3">
          <label class="form-label">id<span class="text-danger">*</span></label>
          <input type="text" v-model="props.dto.id" class="form-control" disabled/>
        </div>

        <div class="mb-3">
          <label class="form-label">배송지<span class="text-danger">*</span></label>
          <div class="d-flex mb-2">
            <input type="text" v-model="props.dto.postCode" class="form-control me-2" style="max-width: 150px;" placeholder="우편번호" />
            <button type="button" @click="PostCodeApi" class="btn btn-outline-secondary" style="min-width: 120px;">우편번호 찾기</button>
          </div>
          <input type="text" v-model="props.dto.address1" class="form-control mb-2" placeholder="도로명주소" />
          <div class="row mb-3">
            <div class="col-md-6">
              <input type="text" v-model="props.dto.address2" class="form-control" placeholder="상세주소" />
            </div>
            <div class="col-md-6">
              <input type="email" v-model="props.dto.address3" class="form-control" placeholder="참고항목" />
            </div>
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-md-6">
            <label class="form-label">Phone<span class="text-danger">*</span></label>
            <input type="text" v-model="props.dto.phone" class="form-control" />
          </div>
          <div class="col-md-6">
            <label class="form-label">Email<span class="text-danger">*</span></label>
            <input type="email" v-model="props.dto.email" class="form-control" />
          </div>
        </div>

        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
          <button type="button" @click="emits('close')" class="btn btn-primary secondary me-2">닫기</button>
          <button type="button" @click="update" class="btn btn-success">저장</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-update-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.card {
  max-width: 100%;
  margin: 0 auto;
}

.card-header {
  background-color: #f8f9fa;
  font-weight: bold;
}
</style>