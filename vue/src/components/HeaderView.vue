<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { ref, watch, computed } from 'vue';
  import { RouterLink, useRouter } from 'vue-router';

  const router = useRouter();
  const result = ref("");
  const drawer = ref(false);
  const store = useStore();
  const { member,cart } = storeToRefs(store);

  watch(() => member.value.id,
    (newValue, oldValue) => {
      store.getCartCount(newValue);
    },
    { deep: true },
  );

  const cnt = computed(() => {
    return cart.value.count > 0 ? cart.value.count : "";
  });

  const logout = async () => {
    result.value = await commonApi("/api/member/logout", "POST");
    console.log(result.value);

    localStorage.clear();
    
    member.value.id = null;
    member.value.sessionId = null;
    member.value.role = null;

    window.alert("logout");
    router.push("/");
  }
</script>

<template>
  <v-app>
    <v-app-bar app>
      <v-app-bar-nav-icon @click="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title>app</v-toolbar-title>
      <template v-slot:append>
        <v-btn v-if="member.id !== null" icon="mdi-account-check" @click="logout"></v-btn>
        <v-btn v-else icon="mdi-account" to="/member/login"></v-btn>
        <v-btn v-if="member.id === null" icon="mdi-account-plus" to="/member/insert"></v-btn>
        <!-- <v-btn left icon="mdi-cart" to="/cart/list"></v-btn> -->
        <v-btn to="/cart/list">
          <v-icon left>mdi-cart</v-icon>
          <span>{{ cnt }}</span>
        </v-btn>
     </template>
    </v-app-bar>

    <v-navigation-drawer v-model="drawer" app>
      <v-list v-if="member.id !== null && member.role.endsWith('ADMIN')">
        <v-list-item link to="/admin">test</v-list-item>
      </v-list>
      <v-list v-else>
        <v-list-item link to="/">home</v-list-item>
        <v-list-item link to="/board/list">board</v-list-item>
        <v-list-item link to="/item/list">item</v-list-item>
        <v-list-item link to="/item/insert">상품등록</v-list-item>
        <v-list-item link to="/order/list">order</v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-main>
      <v-container>
        <router-view></router-view>
      </v-container>
    </v-main>

  </v-app>  
</template>

<style>
  
</style>