<script>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { mapState } from 'pinia';

  export default {
    name: 'GetView',
    data() {
      return {
        mainSrc: {},
        subSrc: {},
        result : {},
        idx: 0,
        active : "",
        input: {
          ino: "", 
          id: "",
          amount: 1,
          price: "",
        },
        store : useStore(),
      };
    },
    mounted() {
      this.get(this.ino);
    },
    computed: {
      ...mapState(useStore, ["member", "cart"]),
      priceFormat() {
        return price => price ? price.toLocaleString() : '';
      },
      realPrice() {
        return this.result.discount > 0 ? 
        this.result.price * (100 - this.result.discount) / 100 * this.input.amount 
        : this.result.price * this.input.amount;
      },
      increase() {
        return value => this.input.amount < 10 ? this.input.amount++ : this.input.amount = 10;
      },
      decrease() {
        return value => this.input.amount > 1 ? this.input.amount-- : this.input.amount = 1;
      },
    },
    beforeDestroy() {
      // 메인 이미지 URL 메모리에서 제거
      URL.revokeObjectURL(this.$refs.mainImage.src);
      // 모든 서브 이미지 URL 메모리에서 제거
      this.result.attachList.forEach((item, i) => {
        URL.revokeObjectURL(this.$refs.subImage[i].src);
      });
    },
    methods: {
      async get(ino) {
        try {
          const res = await commonApi(`/api/item/${ino}`, 'GET');

          console.log(res);

          if (res.status == 200) {
              this.result = res.data;
              this.result.attachList.forEach((item, i) => {
                this.getImage(item, i);
              });
          }
          
        } catch (e) {
          console.log(e);
        }
      },
      async getImage(image, i) {
        try {
          const params = {
            filePath: image.filePath,
            fileName: image.fileName
          };

          const res = await commonApi("/api/file/getFile", "GET", params);

          // console.log(res.data);
          // url 생성
          const url = URL.createObjectURL(res.data);
          
          if (i === 0) {
            this.mainSrc.url = url;
            this.$refs.mainImage.src = url;
          } 

          this.subSrc.url = url;
          this.$refs.subImage[i].src = url;

        } catch (e) {
          console.log(e);
        }
      },
      // increase() {
      //   this.input.amount++;
      //   if (this.input.amount > 10) this.input.amount = 10;
      // },
      // decrease() {
      //   this.input.amount--;
      //   if (this.input.amount < 1) this.input.amount = 1;
      // },
      async addCart() {
        this.input.ino = this.ino;
        this.input.id = this.member.id;
        this.input.price = this.realPrice;

        console.log(this.input);

        const res = await commonApi("/api/cart/insert", "post", this.input);

        if (res.status === 201 || res.status === 200) {
            alert("insert");

            this.store.getCartCount(this.input.id);
        }
      },
      imageChange(e, i) {
        this.$refs.mainImage.src = this.$refs.subImage[i].src;
        // this.idx = i;
      },
      updateItem() {
        this.$router.push("/item/update/" + this.ino);
      },
      async deleteItem() {
        if (confirm("삭제 하시겠습니까?")) {
          const param = {
            ino : this.ino
          };

          const res = await commonApi("/api/item/delete", "DELETE", param);
          
          // console.log(res);

          if (res.status === 200) {
            alert("delete");
            this.$router.push("/item/list");
          }
        }
      },
    },
    props: {
      ino: [Number, String]
    },
  }
</script>

<template>
  <div class="container my-5">
    <div class="row">
      <!-- Product Images -->
      <div class="col-md-6 d-flex flex-column align-items-center">
        <!-- Main Image -->
         <!-- :src="`http://localhost:8081/api/file/getFile?filePath=${result.attachList[0].filePath}&fileName=${result.attachList[0].fileName}`" -->
        <img
          v-if="result.attachList && result.attachList.length"
          :src="mainSrc"
          ref="mainImage"
          alt="메인 이미지"
          class="img-fluid mb-3"
          style="max-height: 350px; object-fit: contain;"
        />
        <div v-else class="bg-light d-flex align-items-center justify-content-center mb-3" style="width:100%; height:350px;">
          <span class="text-muted">이미지가 없습니다</span>
        </div>
        <!-- Thumbnails -->
        <div v-if="result.attachList && result.attachList.length > 0" class="d-flex gap-2">
          <!-- :src="`http://localhost:8081/api/file/getFile?filePath=${item.filePath}&fileName=${item.fileName}`" -->
          <img
            v-for="(item, i) in result.attachList"
            :key="item.attachId"
            :src="subSrc"
            ref="subImage"
            alt="썸네일"
            :index="i"
            class="img-thumbnail"
            :class="i === idx ? 'border-primary' : ''"
            style="width: 70px; height: 70px; object-fit: cover; cursor: pointer;"
            @click="imageChange($event, i)"
          />
        </div>
      </div>
      <!-- Product Details -->
      <div class="col-md-6">
        <h2 class="mb-3">{{result.name}}</h2>
        <p class="text-muted">카테고리: {{result.category}}</p>
        
        <h4 class="text-primary mb-3">{{ priceFormat(realPrice) }}</h4>
        <!-- <p>
          이 상품은 최신 기술이 적용된 고품질 제품입니다. 다양한 기능과 세련된 디자인으로 일상에 편리함을 더해줍니다.
        </p>
        <ul class="list-group mb-3">
          <li class="list-group-item">특징 1: 고성능</li>
          <li class="list-group-item">특징 2: 합리적인 가격</li>
          <li class="list-group-item">특징 3: 1년 무상 A/S</li>
        </ul> -->
        <div class="mb-3 d-flex align-items-center">
          <label for="amount" class="form-label me-3 mb-0">수량</label>
          <button type="button" class="btn btn-outline-secondary" @click="decrease">
            <i class="bi bi-dash"></i>
          </button>
          <span class="mx-3">{{ input.amount }}</span>
          <button type="button" class="btn btn-outline-secondary" @click="increase">
            <i class="bi bi-plus"></i>
          </button>
        </div>
        <button class="btn btn-primary" @click="addCart">장바구니 담기</button>
        <div v-if="this.member.id.startsWith('admin')" class="mt-3 d-flex gap-2">
          <button class="btn btn-warning" @click="updateItem">상품 수정</button>
          <button class="btn btn-danger" @click="deleteItem">상품 삭제</button>
        </div>
      </div>
    </div>
    <!-- Product Description Tabs -->
    <div class="row mt-5">
      <div class="col-12">
        <ul class="nav nav-tabs" id="descTab" role="tablist">
          <li class="nav-item" role="presentation">
            <button
              class="nav-link active"
              id="desc-tab"
              data-bs-toggle="tab"
              data-bs-target="#desc"
              type="button"
              role="tab"
              aria-controls="desc"
              aria-selected="true"
            >
              상세 설명
            </button>
          </li>
          <li class="nav-item" role="presentation">
            <button
              class="nav-link"
              id="review-tab"
              data-bs-toggle="tab"
              data-bs-target="#review"
              type="button"
              role="tab"
              aria-controls="review"
              aria-selected="false"
            >
              리뷰
            </button>
          </li>
        </ul>
        <div class="tab-content p-3 border border-top-0" id="descTabContent">
          <div
            class="tab-pane fade show active"
            id="desc"
            role="tabpanel"
            aria-labelledby="desc-tab"
          >
            <p>
              {{result.content}}
            </p>
          </div>
          <div
            class="tab-pane fade"
            id="review"
            role="tabpanel"
            aria-labelledby="review-tab"
          >
            <p>아직 등록된 리뷰가 없습니다.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 추가적인 스타일이 필요하면 여기에 작성 */
</style>