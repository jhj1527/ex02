<script setup>
  import InsertView from '../item/InsertView.vue';
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref} from 'vue';
  import { useRouter, onBeforeRouteLeave } from 'vue-router';
  import UpdateView from '../item/UpdateView.vue';

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);

  const result = ref([]);
  const itemsPerPage = ref(5);
  const insertView = ref(true);
  const rowObj = ref({});
  const input = reactive({
    ino : "",
    name: "",
    price: 0,
    discount: 0,
    amount : 0,
    category: "",
    content: "",
    attachList: [],
  });

  const selectList = [
    { title: '의류', value: 'clothes' }, 
    { title: '모자', value: 'cap' },
    { title: '신발', value: 'shoes' },
  ];

  const mainFileResult = ref("");
  const multiFileResult = ref([]);
  const file = ref(null);
  const files = ref(null);
  const mainSrc = ref([]);
  const subSrc = ref([]);
  let param = {};
  const form = ref(null);

  const headers = [
    { title: "category", key: "category", },
    { title: "name", key: "name", },
    { title: "price", key: "price", },
    { title: "discount", key: "discount", },
    { title: "amount", key: "amount", },
    { title: "regDate", key: "regDate", },
  ];

  onMounted(() => {
    itemList();
  });

  onBeforeRouteLeave((to, from, next) => {
    // 뒤로가기 시 실행될 로직
    // 메인 이미지와 썸네일 이미지가 있다면 삭제
    // if (mainFileResult.value) {
    //   deleteFile(mainFileResult.value);
    // }

    // if (multiFileResult.value.length > 0) {
    //   multiFileResult.value.forEach((item, i) => {
    //     deleteFiles(item, i);
    //   });
    // }

    // // 모든 이미지 URL 메모리에서 제거
    // mainSrc.value.forEach((item) => {
    //   URL.revokeObjectURL(item);
    // });

    next();
  });

  const itemList = async () => {
    try {
      const res = await commonApi("/api/admin/item", "get");
      result.value = res.data;

    } catch (e) {
      console.log(e);
    }
  };

  const fileRule = ref([
    value => {
      if (!value) {
        return true;

      } else if (!!value && input.category !== "") { 
        return true;

      } else if (!!value && input.category === "") { 
        return 'select category first';
      }  
    },
  ]);

  const getImage = async (item, i) => {
    try {
      param = {};
      param.filePath = item.filePath;
      param.fileName = item.fileName;
  
      const res = await commonApi("/api/file/getFile", "get", param);
      console.log(res.data);
      
      const url = URL.createObjectURL(res.data); // url 생성
      if (i < 0) {
        mainSrc.value[0] = url;
        
      } else {
        subSrc.value.push(url);
        // subSrc.value[i] = url;
      }
      
    } catch (e) { 
      console.log(e);
    }
  };

  const mainFileChange = async(e) => {
    if (mainFileResult.value) {
      await commonApi("/api/file/delete", "POST", mainFileResult.value);
    }
    
    mainFileResult.value = "";
    mainSrc.value = [];

    if (e.target.files.length === 0) {
      return;
    }

    if (input.category === "") {
      alert("select category first");
      // file.value = null;
      return;
    }

    const formData = new FormData();
    formData.append('file', e.target.files[0]);
    formData.append("folderPath" , input.category);

    try {
      const res = await commonApi("/api/file/mainUpload", "POST", formData);

      mainFileResult.value = res.data;

      getImage(mainFileResult.value, -1);

    } catch (e) {
      console.log("error : ", e);
    }
  };

  const multiFileChange = async(e) => {
    if (multiFileResult.value.length > 0) {
      multiFileResult.value.forEach(async(item, i) => {
        await commonApi("/api/file/delete", "POST", item);
      });
    }
    
    multiFileResult.value = [];
    subSrc.value = [];
    
    if (files.value.length === 0) {
      return;
    }
    // console.log(e.target.files);
    const formData = new FormData();

    for (let i = 0; i < files.value.length; i++) {
      formData.append('file', files.value[i]);
    }

    formData.append("folderPath" , input.category);

    // for (let key of formData.keys()) {
    //   console.log(formData.get(key));
    // }

    try {
      const res = await commonApi("/api/file/multiUpload", "POST", formData);
      multiFileResult.value = res.data;

      // input.attachList = input.attachList.concat(res.data);
      console.log(multiFileResult.value);

      multiFileResult.value.forEach((item, i) => {
        getImage(item, i);
      });

    } catch (e) {
      console.log("error : ", e);
    }
  };

  const deleteFile = async(item) => {
    try {
      const res = await commonApi("/api/file/delete", "POST", item);

      mainFileResult.value = "";
      mainSrc.value = null;
      file.value = null;
  
    } catch (e) {
      console.log("error : ", e);
    }
  }

  const deleteFiles = async(item, i) => {
    try {
      const res = await commonApi("/api/file/delete", "POST", item);

      // 해당 인덱스의 파일 객체 삭제
      multiFileResult.value = multiFileResult.value.filter(v => 
        v.attachId !== item.attachId
      );

      subSrc.value.splice(i, 1);
      
      if (multiFileResult.value.length === 0) {
        files.value = null;
      } 
  
    } catch (e) {
      console.log("error : ", e);
    }
  }

  const submitForm = () => {
    try {
      form.value?.validate().then(async (response) => {
        // console.log(response);
        
        if (!response.valid) {
          alert(response.errors[0].errorMessages[0]);
          return;
        }

        if (mainFileResult.value) {
          input.attachList.push(mainFileResult.value);
        }

        if (multiFileResult.value.length > 0) {
          input.attachList = input.attachList.concat(multiFileResult.value);
        }

        // console.log(input.attachList);

        const res = await commonApi("/api/item/update", "patch", input);

        if (res.status === 200 || res.status === 201) {
          alert("update");
          clear();
        }

      }).catch((e) => {
        console.log(e);
      });

    } catch (e) {
      console.log(e);
    }
  }

  const rowDoubleClick = (event, row) => {
    insertView.value = false;
    // rowObj.value = row.item;
    input.ino = row.item.ino || "";
    input.name = row.item.name || "";
    input.price = row.item.price || 0;
    input.discount = row.item.discount || 0;
    input.amount = row.item.amount || 0;
    input.category = row.item.category || "";
    input.content = row.item.content || "";
    mainFileResult.value = "";  
    multiFileResult.value = [];
    mainSrc.value = [];
    subSrc.value = [];

    row.item.attachList.forEach((item, i) => {
      if (item.fileName.startsWith("main_")) {
        mainFileResult.value = item;
        getImage(item, -1);

      } else {
        multiFileResult.value.push(item);
      }
    });

    multiFileResult.value.forEach((item, i) => {
      getImage(item, i);
    });
  };
  
  const insert = () => {
    alert("insert");
    clear();
  };

  const clear = () => {
    insertView.value = true;
    // 초기화
    input.ino = "";
    input.name = "";
    input.price = 0;
    input.discount = 0;
    input.amount = 0;
    input.category = "";
    input.content = "";
    input.attachList = [];
    mainFileResult.value = null;  
    multiFileResult.value = [];
    file.value = null;
    files.value = null;
    mainSrc.value = [];
    subSrc.value = [];

    itemList();
  };
</script>
<template >
  <v-container>
    <v-row>
      <v-col cols="12" sm="6" md="6">
        <v-card>
          <v-card-text></v-card-text>
            <v-data-table
              :headers="headers"
              :items="result"
              show-select
              v-model:items-per-page="itemsPerPage"
              :items-per-page-options="[
                { value: 5, title: '5' },
                { value: 10, title: '10' },
                { value: 20, title: '20' },
                { value: -1, title: 'All' }
              ]"
              @dblclick:row="rowDoubleClick"
            >
              <template v-slot:item.category="{ item }">
                {{ item.category }}
              </template>

              <template v-slot:item.name="{ item }">
                {{ item.name }}
              </template>

              <template v-slot:item.price="{ item }">
                {{ item.price }}
              </template>

              <template v-slot:item.discount="{ item }">
                {{ item.discount }}
              </template>

              <template v-slot:item.amount="{ item }">
                {{ item.amount }}
              </template>

              <template v-slot:item.regDate="{ item }">
                {{ item.regDate }}
              </template>

            </v-data-table>
        </v-card>

      </v-col>

      <v-col cols="12" sm="6" md="6">
        <InsertView v-if="insertView" @insert="insert"></InsertView>
        <nav v-else>
          <!-- <UpdateView v-else :dto="rowObj"></UpdateView> -->
          <v-form ref="form" @submit.prevent="submitForm">
            <v-card class="pa-4">
              <v-card-title>상품 수정</v-card-title>
              <v-card-text>
                <v-text-field 
                  clearable
                  v-model="input.name"
                  label="상품명"
                  class="mb-2"
                  prepend-icon="mdi-pencil"
                  required
                  :rules="[
                    // rules.required(),
                    value => !!value || 'name required field',
                  ]"
                ></v-text-field>

                <v-number-input
                  v-model="input.price"
                  control-variant="hidden"
                  prepend-icon="mdi-pencil"
                  label="가격"
                  class="mb-2"
                  :min="0"
                  :rules="[
                    value => !!value || 'price required field',
                    value => value >= 100 && value <= 1000 || 'price must be between 100 and 1000'
                  ]"
                ></v-number-input>

                <v-number-input 
                  v-model="input.discount"
                  control-variant="hidden"
                  prepend-icon="mdi-pencil"
                  label="할인율"
                  class="mb-2"
                  required 
                  :max="100"
                  :min="0"
                ></v-number-input>

                <v-number-input 
                  v-model="input.amount"
                  control-variant="hidden"
                  prepend-icon="mdi-pencil"
                  label="수량"
                  class="mb-2"
                  required 
                ></v-number-input>

                <v-select
                  v-model="input.category"
                  prepend-icon="mdi-form-select"
                  label="카테고리"
                  :items="selectList"
                  item-title="title"
                  item-value="value"
                  required
                ></v-select>

                <v-textarea
                  v-model="input.content"
                  prepend-icon="mdi-pencil"
                  label="상품 설명"
                  required
                ></v-textarea>

                <v-file-input
                  v-model="file"
                  @change="mainFileChange"
                  label="메인 이미지"
                  accept="image/*"
                  :rules="fileRule"
                ></v-file-input>

                <v-list v-if="mainFileResult">
                  <v-list-item>
                    <!-- <v-list-item-title>{{ mainFileResult.fileName }}</v-list-item-title> -->
                    <v-img 
                    :src="mainSrc[0]"
                    :height="100" 
                    :width="100"
                    cover
                    ></v-img>
                    <template v-slot:append>
                      <v-btn icon="mdi-delete" variant="text" @click="deleteFile(mainFileResult)"></v-btn>
                    </template>
                  </v-list-item>
                </v-list>

                <v-file-input
                  v-model="files"
                  @change="multiFileChange"  
                  label="썸네일"
                  accept="image/*"
                  multiple
                ></v-file-input>

                <v-list v-if="multiFileResult">
                  <v-list-item v-for="(item, i) in multiFileResult" :key="item.attachId">
                    <!-- <v-list-item-title>{{ item.fileName }}</v-list-item-title> -->
                    <v-img 
                      :src="subSrc[i]"
                      :height="100" 
                      :width="100"
                      class="ml-2"
                    ></v-img>
                    <template v-slot:append>
                      <v-btn icon="mdi-delete" variant="text" @click="deleteFiles(item, i)"></v-btn>
                    </template>
                  </v-list-item>
                </v-list>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="primary" variant="text" type="submit" text="수정"></v-btn>
              </v-card-actions>
            </v-card>
          </v-form>
        </nav>
      </v-col>
    </v-row>
  </v-container>
</template>
<style scoped>

</style>