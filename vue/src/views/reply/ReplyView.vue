<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { defineProps, defineEmits, reactive, ref, onMounted, computed } from 'vue';
  import { useRouter } from 'vue-router';
  import dayjs from 'dayjs';
  import { shallowRef } from 'vue';


  const dialog = shallowRef(false);
  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  // const emits = defineEmits(["close", "update"]);
  const props = defineProps({
    bno: [Number, String],
  });
  let param = {};
  let numbers = [];
  const result = ref("");
  const page = ref("");
  const input = reactive({
    bno: props.bno,
    reply: "",
    id: member.value.id,
  }); 
  const modal = reactive({
    dto: {},
  });

  onMounted(() => {
    replyList();
  });

  const replyList = async () => {
    try {
      let res = "";
      if (Object.keys(page.value).length === 0) {
        res = await commonApi(`/api/reply/list/${props.bno}/1`, 'get');
        
      } else {
        res = await commonApi(`/api/reply/list/${props.bno}/${page.value.criteriaDto.pageNum}`, 'get');
      }

      // const res = await commonApi(`/api/reply/list/${props.bno}/1`, 'get');
      console.log(res.data);
      page.value = res.data.page;
      result.value = res.data.list;

      numbers = [];

      for (let i = page.value.startPage; i <= page.value.endPage; i++) {
        numbers.push(i);
      }
      
    } catch (e) {
      console.error(e);
    }
  };

  const insert = async () => {
    try {
      const res = await commonApi('/api/reply/insert', 'post', input);
      
      if (res.data === 1) {
        alert("insert");
        replyList();
        input.reply = "";

      } else {
        alert("error");
      }

    } catch (e) {
      console.error(e);
    }
  };

  const update = async () => {
    try {
      const res = await commonApi("/api/reply/update", "patch", modal.dto);
          
      if (res.data === true) {
        alert("update");
        replyList();

      } else {
        alert("error");
      }

    } catch (e) {
      console.error(e);
    }
  };

  const remove = async (rno) => {
    try {
        const res = await commonApi("/api/reply/delete/" + rno, "DELETE");
  
        if (res.data === true) {
          alert("delete");
          result.value = result.value.filter(item => item.rno !== rno);

        } else {
          alert("error");
        }
        
      } catch (e) {
        console.error(e);
      }
  };

  const popup = (item) => {
    // input.reply = JSON.parse(JSON.stringify(item.reply));
    modal.dto = { ...item };
  };

  const pageClick = (pageNum) => {
    page.value.criteriaDto.pageNum = pageNum;
    replyList();
  };

</script>

<template>
  <!-- 댓글 작성 폼 -->
  <!-- <v-container> -->
    <v-card class="mt-5">
      <v-card-title class="text-h6">
        댓글 작성
      </v-card-title>
      <v-card-text>
        <v-textarea
          v-model="input.reply"
          label="댓글을 입력하세요"
          outlined
          rows="3"
        ></v-textarea>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="insert" color="primary">
          댓글 작성
        </v-btn>
      </v-card-actions>
    </v-card>
    <!-- 댓글 목록 -->
    <v-card v-for="item in result" :key="item.rno" class="mt-3">
      <v-card-title class="d-flex align-center">
        <span class="text-subtitle-1">{{ item.id }}</span>
        <span class="text-caption ml-2">{{ dayjs(item.regDate).format('YYYY.MM.DD HH:mm:ss') }}</span>
        <v-spacer></v-spacer>
        
        <v-dialog v-if="member.id === item.id" v-model="dialog" max-width="650" >
          <template v-slot:activator="{ props: activatorProps }">
            <v-btn
              class="mr-2"
              size="small"
              color="primary"
              text="수정"
              v-bind="activatorProps"
              @click="popup(item)"
            ></v-btn>
          </template>
          
          <v-card prepend-icon="mdi-account" title="reply" >
            <v-card-text>
              <v-row dense>
                <v-col cols="12" md="4" sm="6">
                  <v-text-field 
                    v-model="modal.dto.id"  
                    label="id*" 
                    required
                    readonly
                    ></v-text-field>
                </v-col>
                <v-col cols="12">
                  <v-textarea
                    v-model="modal.dto.reply"  
                    label="reply"
                    outlined
                    rows="5"
                  ></v-textarea>
                </v-col>
              </v-row>

              <small class="text-caption text-medium-emphasis">*indicates required field</small>
            </v-card-text>

            <v-divider></v-divider>

            <v-card-actions>
              <v-spacer></v-spacer>

              <v-btn
                text="Close"
                variant="plain"
                @click="dialog = false"
              ></v-btn>

              <v-btn
                color="primary"
                text="Save"
                variant="tonal"
                @click="update"
              ></v-btn>
            </v-card-actions>
          </v-card>
        </v-dialog>
        
        <!-- <v-btn v-if="member.id === item.id" size="small" color="primary" class="mr-2">수정</v-btn> -->
        <v-btn v-if="member.id === item.id" @click="remove(item.rno)" size="small" color="error">삭제</v-btn>
      </v-card-title>
      <v-card-text>
        {{ item.reply }}
      </v-card-text>
    </v-card>

    <!-- 페이지네이션 -->
    <div class="text-center mt-4">
      <v-pagination
        v-if="page"
        v-model="page.criteriaDto.pageNum"
        :length="numbers.length"
        @update:model-value="pageClick"
      ></v-pagination>
    </div>
  <!-- </v-container> -->


    
</template>

<style>

</style>