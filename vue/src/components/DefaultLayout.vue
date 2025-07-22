<script setup>
  import HeaderView from './HeaderView.vue';
  import Footerview from './Footerview.vue';
</script>

<template>
  <HeaderView />
  <slot></slot>
  <Footerview/>
</template>

<style>

</style>
