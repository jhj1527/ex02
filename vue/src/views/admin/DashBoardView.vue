<script setup>
  import { commonApi } from '@/service/common';
  import { useStore } from '@/stores/store';
  import { storeToRefs } from 'pinia';
  import { computed, onMounted, reactive, ref, } from 'vue';
  import { useRouter } from 'vue-router';
  import dayjs from 'dayjs';
  import BarChartView from '@/components/BarChartView.vue';
  import PieChartView from '@/components/PieChartView.vue';

  const router = useRouter();
  const store = useStore();
  const { member } = storeToRefs(store);
  const avgPrice = ref(0);
  const result = ref({});
  const barChartResult = reactive({
    data: [],
    barChartData: {},
    barChartOptions: {},
    isLoading: true,
  });
  const pieChartResult = reactive({
    data: [],
    pieChartData: {},
    pieChartOptions: {},
    isLoading: true,
  });
  const dashBoardResult = reactive({
    data: {},
  });
  const memberResult = reactive({
    data: [],
  });
  const orderResult = reactive({
    data: [],
  });
  const firstDay = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
  let param = [];
  const category = reactive({
    cap: null,
    clothes: null,
    shoes: null,
  });
  const attribute = reactive({
    itemsPerPage : 4,
    search : "",
  });

  const selected = ref([]);

  const headers = [
    // { title: "check", key: "check"},
    { title: "주문일자", align: "center", key: "regDate", },
    { title: "orderId", align: "center",key: "orderId"},
    { title: "name", align: "center",key: "name" },
    { title: "id", align: "center",key: "id" },
    { title: "price", align: "center",key: "price"},
    { title: "qty", align: "center",key: "quantity"},
    { title: "state", align: "center",key: "state", sortable: false },
    { title: "actions", align: "center",key: "actions", sortable: false },
    // { title: "액션", key: "actions", sortable: false, width: 80 }
  ];

  onMounted(() => {
    getList();
  });

  const stateFormat = computed(() => (state) => {
    if (state === 1) {
      state = "배송준비";

    } else if (state === 2) {
      state = "배송중";

    } else if (state === 3) {
      state = "배송완료";

    } else if (state === 4) {
      state = "주문취소";
    }
    return state;
  });

  const dateFormat = computed(() => 
    (item) => item ? dayjs(item).format('YYYY-MM-DD HH:mm:ss') : ''
  );

  const priceFormat = computed(() =>
    (value) => value > 0 ? value.toLocaleString() : 0
  );

  const chartSumPrice = computed(() => {
    return barChartResult.data?.reduce((sum, item) => sum += item.sumPrice, 0);
  });

  const getList = async () => {
    try {
      const res = await commonApi("/api/admin/chart", "get");
      result.value = res.data;
      dashBoardResult.data = res.data.dashboard;
      memberResult.data = res.data.member;
      orderResult.data = res.data.order;

      barChart(result.value.barChart);
      pieChart(result.value.pieChart);

      

    } catch (e) {
      console.log(e);
    }
  };

const barChart = (data) => {
  barChartResult.data = data;
  avgPrice.value = Math.round(chartSumPrice.value / barChartResult.data.length);

  barChartResult.data?.map(item => {
    item.month = new Date(item.month).toLocaleDateString('en-US', { month: 'short' });
    return item;
  });

  // console.log(barChartResult.data);

  barChartResult.barChartData = {
    labels: barChartResult.data?.map(item => item.month),
    datasets: [
      {
        type: 'bar',
        label: '판매량',
        data: barChartResult.data?.map(item => item.sumPrice),
        backgroundColor: [
          'rgba(255, 99, 132, 0.2)',
          'rgba(255, 159, 64, 0.2)',
          'rgba(255, 205, 86, 0.2)',
          'rgba(75, 192, 192, 0.2)',
          'rgba(54, 162, 235, 0.2)',
          'rgba(153, 102, 255, 0.2)',
        ],
        borderColor: [
          'rgb(255, 99, 132)',
          'rgb(255, 159, 64)',
          'rgb(255, 205, 86)',
          'rgb(75, 192, 192)',
          'rgb(54, 162, 235)',
          'rgb(153, 102, 255)',
        ],
        borderWidth: 1,
        fill: false,
      },
      {
        type: 'line',
        label: '평균',
        data: [],
        fill: false,
        borderColor: 'rgb(255, 255, 255)',
        borderWidth: 0.5,
      }
    ]
  };

  barChartResult.data.forEach(item => {
    barChartResult.barChartData.datasets[1].data.push(avgPrice.value);
  });

  barChartResult.barChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
  };

  barChartResult.isLoading = false;
};

const pieChart = (data) => {
  pieChartResult.data = data;

  pieChartResult.data.forEach(item => {
    if (item.category === "cap") {
      category.cap = item.category;

    } else if (item.category === "clothes") {
      category.clothes = item.category;

    } else if (item.category === "shoes") {
      category.shoes = item.category;
    }
  });

  if (category.cap === null) {
    param = {};
    param = {
      month: pieChartResult.data[0]?.month || dayjs(firstDay).format("YYYY-MM-DD"),
      category: "cap",
      count: 0,
      percent: 0.0,
    };
    pieChartResult.data.push(param);
  }

  if (category.clothes === null) {
    param = {};
    param = {
      month: pieChartResult.data[0]?.month || dayjs(firstDay).format("YYYY-MM-DD"),
      category: "clothes",
      count: 0,
      percent: 0.0,
    };
    pieChartResult.data.push(param);
  }

  if (category.shoes === null) {
    param = {};
    param = {
      month: pieChartResult.data[0]?.month || dayjs(firstDay).format("YYYY-MM-DD"),
      category: "shoes",
      count: 0,
      percent: 0.0,
    };
    pieChartResult.data.push(param);
  }

  // console.log(pieChartResult.data);

    pieChartResult.pieChartData = {
      labels: pieChartResult.data?.map(item => item.category),
      datasets: [
        {
          backgroundColor: [
            'rgba(255, 99, 132, 0.2)',
            'rgba(255, 205, 86, 0.2)',
            'rgba(54, 162, 235, 0.2)',
          ],
          borderColor: [
            'rgb(255, 99, 132)',
            'rgb(255, 205, 86)',
            'rgb(54, 162, 235)',
          ],
          borderWidth: 1,
          data: pieChartResult.data?.map(item => item.percent)
        }
      ]
    };

    pieChartResult.pieChartOptions = {
      responsive: true,
      maintainAspectRatio: false
    };

    pieChartResult.isLoading = false;
  };

  const onClickSeeAll = () => {
    attribute.itemsPerPage = attribute.itemsPerPage === 4 ? memberResult.data.length : 4
  }

  const getStatusColor = (status) => {
    const statusColors = {
      '배송준비': 'secondary',
      '배송중': 'primary',
      '배송완료': 'success',
      '주문취소': 'error'
    }
    return statusColors[status] || 'grey'
  }

  const stateUpdate = async (item) => {
    try {
      console.log(item);
      param = {};
      // param.oino = item.oino;
      // param.state = item.state;
      
      const res = await commonApi("/api/order/updateState", "patch", item);
      if (res.status === 200) {
        alert("update");
        item.state += 1;
      }
      
    } catch (e) {
      console.log(e);
    }
  }

</script>
<template>
  <v-container>
    <v-row>
      <!-- Earnings Card -->
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-caption text-grey">Total Sales</div>
                <div class="text-h5 font-weight-bold">{{ priceFormat(dashBoardResult.data.totalPrice) }}</div>
                <!-- <div class="text-caption text-success">+10%</div> -->
              </div>
              <v-icon color="primary" size="x-large">mdi-currency-krw</v-icon>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Tasks Card -->
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-caption text-grey">Month Sales</div>
                <div class="text-h5 font-weight-bold">{{ priceFormat(dashBoardResult.data.monthPrice) }}</div>
                <!-- <div class="text-caption text-error">-5%</div> -->
              </div>
              <v-icon color="error" size="x-large">mdi-currency-krw</v-icon>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Views Card -->
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-caption text-grey">Today Views</div>
                <div class="text-h5 font-weight-bold">{{ dashBoardResult.data.visitCount }}</div>
                <!-- <div class="text-caption text-success">+8%</div> -->
              </div>
              <v-icon color="success" size="x-large">mdi-eye</v-icon>
            </div>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Downloads Card -->
      <v-col cols="12" sm="6" md="3">
        <v-card elevation="2">
          <v-card-text>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-caption text-grey">Today Sign</div>
                <div class="text-h5 font-weight-bold">{{ dashBoardResult.data.regDateCount }}</div>
                <!-- <div class="text-caption text-warning">+3%</div> -->
              </div>
              <v-icon color="warning" size="x-large">mdi-clipboard-account-outline</v-icon>
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-spacer />

    <v-row>
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-center">최근 6개월</v-card-title>
          <v-card-text class="pa-4 d-flex align-center justify-center" style="height: 300px;">
            <BarChartView v-if="!barChartResult.isLoading" :barChartData="barChartResult.barChartData"
              :barChartOptions="barChartResult.barChartOptions"></BarChartView>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-center" v-html="dayjs(firstDay).format('M') + '월'"></v-card-title>
          <v-card-text class="pa-4 d-flex align-center justify-center" style="height: 300px;">
            <PieChartView v-if="!pieChartResult.isLoading" :pieChartData="pieChartResult.pieChartData"
              :pieChartOptions="pieChartResult.pieChartOptions"></PieChartView>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    
    <v-row>
      <v-col cols="12" sm="6" md="6">
        <v-data-table 
          :items="orderResult.data"
          item-value="oino"
          :headers="headers"
          :items-per-page-options="[
            { value: 5, title: '5' },
            { value: 10, title: '10' },
            { value: 20, title: '20' },
            { value: -1, title: 'All' }
          ]"
        >
          <template v-slot:item="{ item }" >
            <tr class="text-no-wrap">
              <td>{{ dateFormat(item.regDate) }}</td>
              <td>{{ item.orderId }}</td>
              <td>{{ item.name }}</td>
              <td>{{ item.id }}</td>
              <td>{{ priceFormat(item.price) }}</td>
              <td>{{ item.quantity }}</td>
              <td>
                <v-chip
                  :color="getStatusColor(stateFormat(item.state))"
                  variant="outlined"
                  class="font-weight-bold"
                >
                {{ stateFormat(item.state) }}
                </v-chip>
              </td>
              <td>
                <v-btn
                  v-if="item.state === 1"
                  color="error"
                  variant="outlined"
                  @click="stateUpdate(item)"
                  text="배송준비"
                ></v-btn>
                <v-btn
                  v-else-if="item.state === 2"
                  color="warning"
                  variant="outlined"
                  @click="stateUpdate(item)"
                  text="배송중"
                ></v-btn>
              </td>
            </tr>
          </template>

        </v-data-table>

        <pre>{{ selected }}</pre>
      </v-col>

      <v-col cols="12" sm="6" md="6">
        <v-data-iterator :items="memberResult.data" :items-per-page="attribute.itemsPerPage" :search="attribute.search" :filter-keys="['id']">
          <template v-slot:header>
            <h1 class="text-h4 font-weight-bold d-flex justify-space-between mb-4 align-center">
              <v-text-field 
                v-model="attribute.search" 
                density="comfortable" 
                placeholder="Search" 
                prepend-inner-icon="mdi-magnify"
                style="max-width: 300px;" 
                variant="solo" 
                clearable 
                hide-details
              ></v-text-field>
              
              <v-checkbox 
                variant="text" 
                label="Checkbox" 
                @update:modelValue="onClickSeeAll" 
                :disabled="memberResult.data.length > 4 ? false : true"
                :indeterminate="memberResult.data.length > 4 ? false : true"
              ></v-checkbox>
            </h1>
          </template>

          <template v-slot:default="{ items }">
            <v-row>
              <v-col v-for="(item, i) in items" :key="item.id" cols="12" sm="6" xl="3">
                <v-sheet border>
                  <v-list-item :title="item.raw.id" density="comfortable" lines="two" :subtitle="item.raw.regdate">
                    <template v-slot:title>
                      <strong class="text-h6">
                        {{ item.raw.id }}
                      </strong>
                    </template>
                  </v-list-item>
                  <v-table class="text-caption" density="compact">
                    <tbody>
                      <tr align="right">
                        <th>role:</th>
                        <td>{{ item.raw.role }}</td>
                      </tr>

                      <tr align="right">
                        <th>point:</th>
                        <td>{{ item.raw.point }}</td>
                      </tr>

                      <tr align="right">
                        <th>boardCnt:</th>
                        <td>{{ item.raw.boardCount }}</td>
                      </tr>

                      <tr align="right">
                        <th>replyCnt:</th>
                        <td>{{ item.raw.replyCount }}</td>
                      </tr>

                      <tr align="right">
                        <th>orderCnt:</th>
                        <td>{{ item.raw.orderCount }}</td>
                      </tr>
                    </tbody>
                  </v-table>
                </v-sheet>
              </v-col>
            </v-row>
          </template>

          <template v-slot:footer="{ page, pageCount, prevPage, nextPage }">
            <div class="d-flex align-center justify-center pa-4">
              <v-btn :disabled="page === 1" density="comfortable" icon="mdi-arrow-left" variant="tonal" rounded
                @click="prevPage"></v-btn>

              <div class="mx-2 text-caption">
                Page {{ page }} of {{ pageCount }}
              </div>

              <v-btn :disabled="page >= pageCount" density="comfortable" icon="mdi-arrow-right" variant="tonal" rounded
                @click="nextPage"></v-btn>
            </div>
          </template>
        </v-data-iterator>
      </v-col>
    </v-row>

  </v-container>
</template>
<style scoped></style>