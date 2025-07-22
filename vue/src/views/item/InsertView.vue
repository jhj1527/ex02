<script setup>
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, reactive, ref, useTemplateRef } from 'vue'
  import { useRouter } from 'vue-router';
  import { commonApi } from '@/service/common';
  import { useRules } from 'vuetify/labs/rules'

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  const rules = useRules();

  const input = reactive({
    name: "",
    price: 0,
    discount: 0,
    category: "",
    content: "",
    attachList: [],
  });

  const selectList = [
    { title: '상의', value: 'top' }, 
    { title: '하의', value: 'bottom' }, 
    { title: '모자', value: 'cap' },
    { title: '신발', value: 'shoes' },
  ];

  const mainFileResult = ref("");
  const multiFileResult = ref([]);
  const file = ref(null);
  const files = ref(null);
  const subImageRefs = ref({});
  const mainImageRefs = ref({});
  const mainSrc = ref([]);
  const subSrc = ref([]);
  let param = {};

  const setMainFileRef = async (el) => {
    if (el) {
      mainImageRefs.value = el;
    }
  };

  const setMultiFileRef = async (el, idx) => {
    if (el) {
      subImageRefs.value[idx] = el;
    }
  };

  const getImage = async (item, i) => {
    try {
      debugger;
      param = {};
      param.filePath = item.filePath;
      param.fileName = item.fileName;
  
      const res = await commonApi("/api/file/getFile", "get", param);
      console.log(res.data);
      
      const url = URL.createObjectURL(res.data); // url 생성
      if (i !== undefined) {
        subSrc.value.push(url);
        subImageRefs.value[i].src = url;

      } else {
        mainSrc.value[0] = url;
        mainImageRefs.value[0].src = url;
      }
      
    } catch (e) { 
      console.log(e);
    }
  };

  const mainFileChange = async(e) => {
    if (mainFileResult.value) {
      await commonApi("/api/file/delete", "POST", mainFileResult.value);
      mainFileResult.value = "";
    }

    if (e.target.files.length === 0) {
      return;
    }

    const formData = new FormData();
    formData.append('file', e.target.files[0]);
    formData.append("folderPath" , input.category);

    try {
      const res = await commonApi("/api/file/mainUpload", "POST", formData);

      mainFileResult.value = res.data;

      getImage(mainFileResult.value);

    } catch (e) {
      console.log("error : ", e);
    }
  };

  const multiFileChange = async(e) => {
    multiFileResult.value = "";
    
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

      // 해당 파일 객체 삭제
      mainFileResult.value = "";
      mainSrc.value[0] = null;
      mainImageRefs.value[0] = null;
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
      subImageRefs.value[i] = null;
      
      if (multiFileResult.value.length === 0) {
        files.value = null;
      } 
  
    } catch (e) {
      console.log("error : ", e);
    }
  }

  const submitForm = async() => {
    try {
      if (mainFileResult.value) {
        input.attachList.push(mainFileResult.value);
      }

      if (multiFileResult.value.length > 0) {
        input.attachList = input.attachList.concat(multiFileResult.value);
      }

      // console.log(input.attachList);

      const res = await commonApi("/api/item/insert", "POST", input);

      if (res.status === 200 || res.status === 201) {
        alert("isnert");

        input.name = "";
        input.price = 0;
        input.discount = 0;
        input.category = "";
        input.content = "";
        input.attachList = [];
        mainFileResult.value = "";  
        multiFileResult.value = [];
        file.value = null;
        files.value = null; 
      } 

  
    } catch (e) {
      console.log("error : ", e);
    }
  }

</script>

<template>
  <v-container>
    <v-form @submit.prevent="submitForm">
      <v-card class="pa-4">
        <v-card-title>상품 등록</v-card-title>
        <v-card-text>
          <v-text-field 
            clearable
            v-model="input.name"
            label="상품명"
            class="mb-2"
            prepend-icon="mdi-pencil"
            required
            :rules="[
              value => !!value || 'required field',
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
              value => !!value || 'required field',
              value => value >= 100 && value <= 1000 || 'must be between 100 and 1000'
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
          ></v-file-input>

          <v-list v-if="mainFileResult">
            <v-list-item>
              <!-- <v-list-item-title>{{ mainFileResult.fileName }}</v-list-item-title> -->
              <v-img 
              :src="mainSrc[0]"
              :ref="el => setMainFileRef(el)" 
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
                :ref="el => setMultiFileRef(el, i)" 
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
          <v-btn color="primary" type="submit">등록</v-btn>
        </v-card-actions>
      </v-card>
    </v-form>
  </v-container>
</template>

<style scoped>
  
</style>