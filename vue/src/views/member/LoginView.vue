<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import axios from 'axios';
  import { storeToRefs } from 'pinia';
  import { reactive, ref, defineEmits } from 'vue';
  import { RouterLink, useRouter } from 'vue-router';

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  const input = reactive({
    id : "",
    password : "",
  });
  const modalCheck = ref(false);
  const result = ref("");
  const emit = defineEmits(["popup"]);
  const visible = ref(false);
  
  const login = async () => {
    try {
      
      result.value = await commonApi("/api/member/login", "POST", input);
  
      if (result.value.status === 200) {
        member.value.id = result.value.data.id;
        member.value.sessionId = result.value.data.sessionId;
        member.value.role = result.value.data.role;
      
        localStorage.setItem("id" , member.value.id);
        localStorage.setItem("sessionId" , member.value.sessionId);
        localStorage.setItem("role" , member.value.role);
  
        console.log(member.value.id);
        console.log(member.value.sessionId);
        console.log(member.value.role);
        
        window.alert("login");
        router.push("/");
        
      } else {
        alert(result.value.data.message);
      }

    } catch (e) {
      console.log(e);
    }
  };

</script>

<template>
  <div>
    <v-img
      class="mx-auto my-6"
      max-width="228"
      src="https://cdn.vuetifyjs.com/docs/images/logos/vuetify-logo-v3-slim-text-light.svg"
    ></v-img>

    <v-card
      class="mx-auto pa-12 pb-8"
      elevation="8"
      max-width="448"
      rounded="lg"
    >
      <div class="text-subtitle-1 text-medium-emphasis">Account</div>

      <v-text-field
        v-model="input.id"
        density="compact"
        placeholder="id"
        prepend-inner-icon="mdi-account"
        variant="outlined"
      ></v-text-field>

      <div class="text-subtitle-1 text-medium-emphasis d-flex align-center justify-space-between">
        Password

        <a
          class="text-caption text-decoration-none text-blue"
          href="#"
          rel="noopener noreferrer"
          target="_blank"
        >
          Forgot login password?</a>
      </div>

      <v-text-field
        v-model="input.password"
        :append-inner-icon="visible ? 'mdi-eye-off' : 'mdi-eye'"
        :type="visible ? 'text' : 'password'"
        density="compact"
        placeholder="Enter your password"
        prepend-inner-icon="mdi-lock-outline"
        variant="outlined"
        @click:append-inner="visible = !visible"
        @keyup.enter="login"
      ></v-text-field>

      <v-card
        class="mb-12"
        color="surface-variant"
        variant="tonal"
      >
        <v-card-text class="text-medium-emphasis text-caption">
          Warning: After 3 consecutive failed login attempts, you account will be temporarily locked for three hours. If you must login now, you can also click "Forgot login password?" below to reset the login password.
        </v-card-text>
      </v-card>

      <v-btn
        @click="login"
        class="mb-8"
        color="blue"
        size="large"
        variant="tonal"
        block
      >
        Log In
      </v-btn>

      <v-card-text class="text-center">
        <RouterLink to="/member/insert" class="text-blue text-decoration-none" rel="noopener noreferrer">
          Sign up now <v-icon icon="mdi-chevron-right"></v-icon>
        </RouterLink>
      </v-card-text>
    </v-card>
  </div>
</template>

<style scoped>

</style>
