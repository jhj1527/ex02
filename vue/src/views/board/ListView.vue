<script setup>
  import { commonApi } from '@/service/common';
  import { RouterLink, useRouter } from 'vue-router';
  import { onMounted, reactive, ref, computed } from 'vue';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import dayjs from 'dayjs';

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  let numbers = [];
  const result = ref([]);
  // const emit = defineEmits(["popup"]);
  const page = ref({});
  const cnt = ref(0);
  const param = ref({
    pageNum : 1,
    amount : 10,
    type: "",
    keyword: "",
  });

  const headers = [
    { title: "번호", key: "bno", width: 100 },
    { title: "제목", key: "title", width: 150 },
    { title: "내용", key: "content", width: 300 },
    { title: "작성자", key: "id", width: 120 },
    { title: "작성일", key: "regDate", width: 120 },
    { title: "조회수", key: "viewCount", width: 90 },
    // { title: "액션", key: "actions", sortable: false, width: 80 }
  ];

  const selectList = [
    { title: '제목', value: 'title' }, 
    { title: '내용', value: 'content' }, 
    { title: '작성자', value: 'id' },
    { title: '제목 + 내용', value: 'tc' },
  ]

  onMounted(() => {
    pageList();
  });

  const pageList = async () => {
    try {
      // const url = "/api/board/list?" + new URLSearchParams(param.value).toString();

      const res = await commonApi("/api/board/list", "get", param.value);
      console.log(res.data);

      param.value = res.data.page.criteriaDto;
      page.value = res.data.page;
      delete page.value.criteriaDto;
      result.value = res.data.list;
      
      // console.log("param : ", param.value);
      // console.log("page : ", page.value);
      // console.log("result : ", result.value);

      numbers = [];

      for (let i = page.value.startPage; i <= page.value.endPage; i++) {
        numbers.push(i);
      }

      // console.log(numbers);

    } catch (e) {
      console.error(e);
    }
  };

  const dayFormat = computed(() => 
    (value) => dayjs(value).format("YYYY.MM.DD hh:mm:ss")
  );
  
  const pageClick = (pageNum) => {
    param.value.pageNum = pageNum;
    pageList();
  };

  const search = (e) => {
    param.value.pageNum = 1;
    pageList();
  };

  const loadItems = (options) => {
    console.log(options);
  };

  const selectChange = (item) => {
    // console.log(item);
  };
  
</script>

<template>
  <v-container>
    <v-card>
      <v-card-title>
        <v-spacer></v-spacer>
        <v-btn color="primary" to="/board/insert">글쓰기</v-btn>
      </v-card-title>

      <v-data-table
        :headers="headers"
        :items="result"
        :items-length="page.total"
        :items-per-page="param.amount"
        :v-model:page="param.pageNum"
        @update:options="loadItems"
        class="elevation-1"
      >
        <template v-slot:top>
          <v-card title="search" flat></v-card>
          <tr>
            <td style="width: 200px;">
              <v-select
                v-model="param.type"
                label="Select"
                :items="selectList"
                item-title="title"
                item-value="value"
                class="ma-2" 
                density="compact"
                @update:modelValue="selectChange"
                hide-details
                style="min-width: 180px;"
              ></v-select>
            </td>
            <td style="width: 300px;">
              <v-text-field
                v-model="param.keyword"  
                class="ma-2" 
                density="compact" 
                placeholder="Search" 
                prepend-inner-icon="mdi-magnify"
                hide-details
                single-line
                variant="outlined"
                @keyup.enter="search"
                style="min-width: 280px;"
              ></v-text-field>
            </td>
          </tr>
        </template>

        <template v-slot:item="{ item }">
          <tr class="text-no-wrap">
            <td><RouterLink :to="`/board/get/${item.bno}`" style="color: white; text-decoration: none;" >{{ item.bno }}</RouterLink></td>               
            <td>{{ item.title }} [{{ item.replyCount }}]</td>
            <td>{{ item.content }}</td>
            <td>{{ item.id }}</td>
            
            <td>{{ dayFormat(item.regDate) }}</td>
            <td>{{ item.viewCount }}</td>
          </tr>
        </template>

        <template v-slot:bottom>
          <v-pagination
            v-model="param.pageNum"
            :length="numbers.length"
            class="my-4"
            @update:model-value="pageClick"
          ></v-pagination>
        </template>

        <!-- <template #item.actions="{ item }">
          <v-btn icon @click="viewPost(item)">
            <v-icon>mdi-eye</v-icon>
          </v-btn>
        </template> -->
      </v-data-table>
    </v-card>
  </v-container>
</template>

<style scoped>
.v-data-table {
  margin-top: 16px;
}
</style>
