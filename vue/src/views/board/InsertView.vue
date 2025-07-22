<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { reactive, ref, defineEmits, onBeforeMount } from 'vue';
  import { useRouter } from 'vue-router';
  
  const router = useRouter();
  const input = reactive({
    title : "",
    content : "",
    id : "",
  });
  const result = ref("");
  const emit = defineEmits(["popup"]);
  const store = useStore();
  const { member } = storeToRefs(useStore());
  const insert = async () => {
    try {
      // if (input.title === "" && input.content === "") {
      //   alert("error");
      //   return;
      // }
      
      result.value = await commonApi("/api/board/insert", "POST", input);
      
      if (result.value.status === 200) {
        // emit("popup", result.value, "insert");
        window.alert("insert");
        router.push("/board/list");

      } else {
        alert("error");
      }

    } catch (e) {
      console.error(e);
    }
  };

  onBeforeMount(() => {
    input.id = member.value.id;
  });


  const goBack = () => {
    router.go(-1);
  }

</script>

<template>
  <div class="write-container">
    <h2>게시글 작성</h2>
    <form @submit.prevent="insert">
      <div class="form-group">
        <label for="title">title</label>
        <input type="text" v-model="input.title" class="form-control" id="title" required/>
      </div>
      <div class="form-group">
        <label for="content">content</label>
        <textarea v-model="input.content" class="form-control" id="content" rows="3"></textarea>
      </div>
      <div class="form-group">
        <label for="title">id</label>
        <input type="text" v-model="input.id" class="form-control" id="title" disabled />
      </div>
      <div class="button-group">
        <button type="submit" class="btn btn btn-outline-primary me-2 btn-submit">등록</button>
        <button type="button" @click="goBack" class="btn btn btn-outline-danger">취소</button>
      </div>
    </form>
  </div>
  
</template>

<style>
.write-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-control {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.button-group {
  margin-top: 20px;
  text-align: center;
}


</style>
