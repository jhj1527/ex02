<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { defineProps, defineEmits, reactive, ref, onMounted, computed } from 'vue';
  import { useRouter } from 'vue-router';
  import dayjs from 'dayjs';
  import { shallowRef } from 'vue'
  import ReplyView from '../reply/ReplyView.vue';
  

  const dialog = shallowRef(false);
  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  // const emits = defineEmits(["close", "update"]);
  const props = defineProps({
    bno: [Number, String],
  });
  const dayFormat = "yyyy-MM-dd";
  const result = ref({});
  const input = reactive({

  });

  onMounted(() => {
    get();
  }); 

  // const dayFormat = computed(() => 
  //   (value) => dayjs(value).format("YYYY.MM.DD hh:mm:ss")
  // );

  const get = async () => {
    try {
      const res = await commonApi("/api/board/" + props.bno, "GET");
      console.log(res.data);
      result.value = res.data;

      // result.value.regDate = dayjs(result.value.regDate).format("YYYY.MM.DD hh:mm:ss");
      // result.value.updateDate = dayjs(result.value.updateDate).format("YYYY.MM.DD hh:mm:ss");

    } catch (e) {
      console.log(e);
    }
  };

  const update = async () => {
    try {
      const res = await commonApi("/api/board/" + props.bno, "PATCH", result.value);

      if (res.data === 1) {
        alert("update");
        get();
      }

    } catch (e) {
      console.log(e);
    }
  };

  const remove = async () => {
    try {
      const res = await commonApi("/api/board/" + props.bno, "DELETE");
      
      if (res.data === 1) {
        alert("delete");
        router.push("/board/list");
      }

    } catch (e) {
      console.log(e);
    }
  };

</script>

<template>
  <v-container>
    <v-card>
      <v-card-title class="text-h5 font-weight-bold">
        게시글 상세
      </v-card-title>

      <v-card-text>
        <v-row>
          <v-col cols="12">
            <v-text-field
              v-model="result.title"
              label="제목"
              outlined
              dense
            ></v-text-field>
          </v-col>
          <v-col cols="4">
            <v-text-field
              v-model="result.id"
              label="작성자"
              readonly
              outlined
              dense
            ></v-text-field>
          </v-col>
          <v-col cols="4">
            <v-text-field
              v-model="result.regDate"
              label="작성일"
              readonly
              outlined
              dense
              type="datetime-Local"
            ></v-text-field>
            <!-- <VDateInput
              v-model="result.regDate"
              label="작성일"
              readonly
              outlined
              dense
            ></VDateInput> -->
          </v-col>
          <v-col cols="4">
            <v-text-field
              v-model="result.updateDate"
              label="수정일"
              readonly
              outlined
              dense
              type="datetime-Local"
            ></v-text-field>
            <!-- <VDateInput
              v-model="result.updateDate"
              label="수정일"
              readonly
              outlined
              dense
            ></VDateInput> -->
          </v-col>
          <v-col cols="12">
            <v-textarea
              v-model="result.content"
              label="내용"
              outlined
              rows="10"
            ></v-textarea>
          </v-col>
        </v-row>
      </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn v-if="result.id === member.id" color="primary" size="small" @click="update">수정</v-btn>
        <v-btn v-if="result.id === member.id" color="error" size="small" @click="remove">삭제</v-btn>
        <v-btn to="/board/list" color="grey" size="small">목록</v-btn>
      </v-card-actions>
    </v-card>

    <ReplyView :bno="props.bno" />
  </v-container>
</template>