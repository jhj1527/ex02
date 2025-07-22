<script setup>
  import { commonApi } from '@/service/common';
  import { reactive, ref, defineEmits } from 'vue';
  
  const input = reactive({
    id : "",
    password : "",
  });
  const result = ref("");
  const emit = defineEmits(["popup"]);
  
  const insert = async () => {
    try {
      if (input.title === "" && input.content === "") {
        alert("error");
        return;
      }
      
      result.value = await commonApi("/api/member/insert", "POST", input);

      if (result.value.status === 200) {
        emit("popup", result.value, "insert");

      } else {
        alert(result.value.data.message);
      }

    } catch (e) {
      console.error(e);
    }
  };

</script>

<template>
  <div class="signup-container">
    <h2>sign up</h2>
    <form @submit.prevent="insert" class="signup-form">
      <div class="form-group">
        <label for="id" class="form-label">id</label>
        <input type="text" v-model="input.id" class="form-control" id="id" placeholder="id">
      </div>

      <div class="form-group">
        <label for="password" class="form-label">password</label>
        <input type="text" v-model="input.password" class="form-control" id="password" placeholder="password">
      </div>

      <button type="submit" class="btn btn-primary submit-btn">register</button>
    </form>
  </div>
</template>

<style scoped>
.signup-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
}

.signup-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

label {
  margin-bottom: 5px;
}

input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.submit-btn {
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn:hover {
  background-color: #45a049;
}
</style>
