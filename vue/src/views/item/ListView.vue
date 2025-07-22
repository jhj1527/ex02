<template>
  <v-container>
    <!-- Tab navigation -->
    <v-tabs v-model="activeTab" class="mb-4">
      <v-tab value="all">전체보기</v-tab>
      <v-tab value="item">아이템</v-tab>
      <v-tab value="collection">컬렉션</v-tab>
      <v-tab value="small">소재</v-tab>
    </v-tabs>

    <!-- Item count and sort options -->
    <v-row class="mb-4" align="center">
      <v-col cols="6">
        <div class="text-body-1">총 231건</div>
      </v-col>
      <v-col cols="6" class="text-right">
        <v-btn-group>
          <v-btn variant="text">신상품순</v-btn>
          <v-btn variant="text">인기상품순</v-btn>
          <v-btn variant="text">낮은가격순</v-btn>
          <v-btn variant="text">높은가격순</v-btn>
        </v-btn-group>
      </v-col>
    </v-row>

    <!-- Product grid -->
    <v-row>
      <v-col v-for="item in items" :key="item.id" cols="6" sm="4" md="3" lg="2">
      <v-card class="mx-auto" flat>
        <v-img :src="item.image" height="200" cover></v-img>
        <v-card-title class="text-subtitle-2">{{ item.name }}</v-card-title>
        <v-card-text>
          <!-- <v-chip v-if="item.isNew" color="grey-darken-1" size="x-small" class="mb-2">SALE</v-chip> -->
          <div class="text-body-2 mb-1">{{ item.price.toLocaleString() }}원</div>
          <div v-if="item.isNew" class="d-flex align-center">
            <span class="text-red">10%</span>
            <span class="ml-2 text-subtitle-1">{{ (item.price * 0.9).toLocaleString() }}원</span>
          </div>
          <div v-else class="d-flex align-center">
            <span class="text-red"><br></span>
            <span class="ml-2 text-subtitle-1"><br></span>
          </div>
          <v-btn color="primary" class="mt-2" block>장바구니</v-btn>
          <v-btn color="grey-darken" class="mt-2" block>상세</v-btn>
        </v-card-text>
      </v-card>
      </v-col>
    </v-row>

    <!-- 페이지네이션 -->
    <div class="text-center mt-4">
      <v-pagination


      ></v-pagination>
    </div>
  </v-container>
</template>

<script setup>
import { ref } from 'vue'

const activeTab = ref('all')

const items = ref([
  {
    id: 1,
    name: 'J.LETTER 미니 리본 락 참 SI',
    price: 28000,
    image: '/path/to/image1.jpg',
    isNew: false
  },
  {
    id: 2,
    name: 'J.LETTER 미니 리본 키 참 GO',
    price: 28000,
    image: '/path/to/image2.jpg',
    isNew: true
  },
  {
    id: 3,
    name: 'MIGNON 폴램 카드지갑 IV',
    price: 98000,
    image: '/path/to/image3.jpg',
    isNew: true
  },
  {
    id: 4,
    name: 'MIGNON 폴램 카드지갑 DB',
    price: 98000,
    image: '/path/to/image4.jpg',
    isNew: true
  },
  {
    id: 5,
    name: 'DIO SM 폴플 토트 DB',
    price: 178000,
    image: '/path/to/image5.jpg',
    isNew: true
  },
  {
    id: 6,
    name: 'J.LETTER 미니 리본 락 참 SI',
    price: 28000,
    image: '/path/to/image1.jpg',
    isNew: true
  },
  {
    id: 7,
    name: 'J.LETTER 미니 리본 키 참 GO',
    price: 28000,
    image: '/path/to/image2.jpg',
    isNew: true
  },
  {
    id: 8,
    name: 'MIGNON 폴램 카드지갑 IV',
    price: 98000,
    image: '/path/to/image3.jpg',
    isNew: true
  },
  {
    id: 9,
    name: 'MIGNON 폴램 카드지갑 DB',
    price: 98000,
    image: '/path/to/image4.jpg',
    isNew: true
  },
  {
    id: 10,
    name: 'DIO SM 폴플 토트 DB',
    price: 178000,
    image: '/path/to/image5.jpg',
    isNew: true
  }
])
</script>

<style scoped>
.v-card-title {
  font-size: 0.9rem !important;
  line-height: 1.4;
}
</style>