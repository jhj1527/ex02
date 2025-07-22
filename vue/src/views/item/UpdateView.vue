<script>
  import { useStore } from '@/stores/store';
  import { mapState } from 'pinia';
  import { commonApi } from '@/service/common';

  export default {
    name: "UpdateView",
    data() {
      return {
        input: {
          name: "",
          price: "",
          discount: "",
          category: "",
          content: "",
          attachList: [],
        },
        result: [],
      };
    },
    computed: {
      ...mapState(useStore, ["member"]),
    },
    mounted() {
      this.get(this.ino);
    },
    beforeRouteLeave(to, from, next) {
      // 뒤로가기 시 실행될 로직
      this.input.attachList = this.input.attachList.filter(file => file.ino === null);

      // console.log(this.input.attachList);

      this.input.attachList.forEach((file, i) => {
        this.deleteFile(i);
      });

      next();
    },
    methods: {
      async get(ino) {
        try {
          const res = await commonApi(`/api/item/${ino}`, 'GET');

          // console.log(res);

          if (res.status == 200) {
             this.input = res.data;
          }

          // console.log(this.input)
          
        } catch (e) {
          console.log(e);
        }
      },
      numberFilter(e, col) {
        // 숫자만 입력 가능하도록 필터링
        if (col.startsWith("price")) {
          this.input.price = this.input.price.replace(/[^0-9]/g, '');
          this.input.price = Number(this.input.price);

        } else if (col.startsWith("discount")) {
          this.input.discount = this.input.discount.replace(/[^0-9]/g, '');
          this.input.discount = Number(this.input.discount);
        }
      },
      async onFileChange(e) {
        this.result = [];
        const formData = new FormData();

        if (e.target.files.length === 0) {
          return;
        }
        
        for (let i = 0; i < e.target.files.length; i++) {
          formData.append('file', e.target.files[i]);
        }

        formData.append("folderPath" , this.input.category);

        try {
          const res = await commonApi("/api/file/upload", "POST", formData);
          this.result = res.data;

          this.result.forEach(file => this.input.attachList.push(file));

          // console.log(this.input.attachList);
        } catch (e) {
          console.log("error : ", e);
        }
      },
      async deleteFile(i) {
        try {
          if (this.input.attachList[i].ino === null) {
            const res = await commonApi("/api/file/delete", "POST", this.input.attachList[i]);
          }
          // 해당 인덱스의 파일 객체 삭제
          this.input.attachList = this.input.attachList.filter(file => file.attachId !== this.input.attachList[i].attachId);

          // console.log(this.input.attachList);
        } catch (e) {
          console.log("error : ", e);
        }
      },
      async update() {
        try {
          // console.log(this.input);

          const res = await commonApi("/api/item/update", "patch", this.input);

          if (res.status === 200) {
            alert("update");
            this.$refs.inputFile.value = null;
            this.get(this.ino);

          } else {
            alert("error");
          }
          
        } catch (e) {
          console.log("error : ", e);
        }
      },
      list() {
        this.$router.push("/item/list");
      },
    },
    props: {
      ino: [Number, String]
    },
  };
</script>

<template>
  <div class="container mt-5">
    <h2 class="mb-4 justify-content-center">상품 수정</h2>
    <!-- <form @submit.prevent="update1"> -->
      <div class="mb-3">
        <label for="name" class="form-label">상품명</label>
        <input type="text" v-model="input.name" class="form-control" id="name" required>
      </div>
      <div class="mb-3">
        <label for="price" class="form-label">가격</label>
        <input type="text" v-model="input.price" @input="numberFilter($event, 'price')" class="form-control" id="price" required>
      </div>
      <div class="mb-3">
        <label for="discount" class="form-label">할인</label>
        <input type="text" v-model="input.discount" @input="numberFilter($event, 'discount')" class="form-control" id="discount" required>
      </div>
      <div class="mb-3">
        <label for="category" class="form-label">카테고리</label>
        <select v-model="input.category" class="form-select" id="category">
          <option value="fruit">과일</option>
          <option value="vegetable">채소</option>
        </select>
      </div>
      <div class="mb-3">
        <label for="description" class="form-label">상품 설명</label>
        <textarea class="form-control" id="description" v-model="input.content" rows="3"></textarea>
      </div>
      <div class="mb-3">
        <label for="image" class="form-label">상품 이미지</label>
        <input type="file" @change="onFileChange($event)" ref="inputFile" class="form-control" id="image" multiple>
      </div>
      <div class="mb-3" v-if="input.attachList.length > 0">
        <label class="form-label">업로드된 이미지 미리보기</label>
        <div class="d-flex flex-wrap gap-2">
          
          <div v-for="(image, i) in input.attachList" :key="image.attchId">
            <img :src="`http://localhost:8081/api/file/getFile?filePath=${image.filePath}&fileName=${image.fileName}`" v-if="image.fileType" alt="업로드 이미지" class="img-thumbnail">
            <p @click="deleteFile(i)" v-html="image.fileName" style="cursor: pointer;"></p>
          </div>
        </div>
      </div>
    <!-- </form> -->
    <div class="mt-3 d-flex gap-2">
        <button @click="update" class="btn btn-primary">수정하기</button>
        <button @click="list" class="btn btn-success">목록</button>
    </div>
  </div>
</template>

<style scoped>
  .container {
    max-width: 500px;
  }
  img {
    width: 100px; 
    height: 100px; 
    object-fit: cover; 
    border: 1px solid #ddd; 
    border-radius: 4px;
  }
</style>