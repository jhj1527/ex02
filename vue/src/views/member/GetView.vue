<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import axios from 'axios';
  import { storeToRefs } from 'pinia';
  import { reactive, ref, defineEmits, onBeforeMount } from 'vue';
  import { RouterLink, useRouter } from 'vue-router';

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);

  const input = reactive({
    id : "",
  });

  const result = ref([]);

  onBeforeMount(() => {
    input.id = member.value.id;
    // console.log(input.id);
    getMember();
  });

  const getMember = async () => {
    try {

      const url = "/api/member/get?" + new URLSearchParams(input).toString();
      result.value = await commonApi(url, "get");
      
      console.log(result.value);

    } catch (e) {
      console.log(e);
    }
  };

</script>


<template>
  <div>
    <h1>aaaaaaaaa</h1>
  </div>
</template>

<style>

</style>
