<script setup>
import { RouterLink, RouterView } from 'vue-router'
import DefaultLayout from './components/DefaultLayout.vue';

</script>

<template>
 <v-app>
    <v-main>
      <DefaultLayout>
        <!-- <RouterView @popup="popup" /> -->
      </DefaultLayout>
    </v-main>
  </v-app>
</template>

<style scoped>

</style>
