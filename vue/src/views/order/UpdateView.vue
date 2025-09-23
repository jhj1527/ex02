<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { defineProps, defineEmits, reactive, ref } from 'vue';

  const dialog = ref(false)
  const address = ref({
    recipient: '',
    phone: '',
    zipCode: '',
    address1: '',
    address2: ''
  })

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
  <v-container>
    <v-dialog v-model="props.isModal" max-width="500px">
      <v-card>
        <v-card-title class="text-center">
          <span class="">배송지 변경</span>
        </v-card-title>

        <v-card-text>
          <v-text-field
            v-model="props.dto.id"
            label="받는 사람"
            required
          ></v-text-field>
          <v-row>
            <v-col cols="4">
              <v-text-field
              v-model="props.dto.postCode"
              label="우편번호"
              readonly
              ></v-text-field>
            </v-col>
            <v-col cols="4">
              <v-btn block
                @click="PostCodeApi" 
                size="large"
                text="우편번호찾기"
                ></v-btn>
            </v-col>
          </v-row>
          
          <v-text-field
            v-model="props.dto.address1"
            label="주소"
            readonly
          ></v-text-field>
          <v-row>
            <v-col cols="6">
              <v-text-field
                v-model="props.dto.address2"
                label="상세주소"
              ></v-text-field>
            </v-col>
            <v-col cols="6">
              <v-text-field
                v-model="props.dto.address3"
                label="참고항목"
                readonly
              ></v-text-field>
            </v-col>
          </v-row>
          <v-text-field
            v-model="props.dto.phone"
            label="Mobile*"
            required
          ></v-text-field>
          <v-text-field
            v-model="props.dto.email"
            label="Email Address*"
            required
          ></v-text-field>
        </v-card-text>
        
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="blue-darken-1" variant="text" @click="emits('close')">취소</v-btn>
          <v-btn color="blue-darken-1" variant="text" @click="update">저장</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>