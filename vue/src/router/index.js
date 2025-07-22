import { useStore } from '@/stores/store';
import { storeToRefs } from 'pinia';
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "home",
      component: HomeView,
    },
    {
      path: "/board/list",
      name: "Boardlist",
      component: () => import('../views/board/ListView.vue'),
    },
    {
      path: "/board/insert",
      name: "boardInsert",
      component: () => import('../views/board/InsertView.vue'),
    },
    {
      path: "/board/get/:bno",
      name: "boardGet",
      component: () => import('../views/board/GetView.vue'),
      props : true,
    },
    {
      path: "/member/insert",
      name: "memberInsert",
      component: () => import('../views/member/InsertView.vue'),
    },
    {
      path: "/member/login",
      name: "login",
      component: () => import('../views/member/LoginView.vue'),
    },
    {
      path: "/member/get",
      name: "memberGet",
      component: () => import('../views/member/GetView.vue'),
    },
    {
      path: "/reply/list",
      name: "replyList",
      component: () => import('../views/reply/ReplyView.vue'),
    },
    {
      path: "/item/list",
      name: "Itemlist",
      component: () => import('../views/item/ListView.vue'),
    },
    {
      path: "/item/insert",
      name: "itemInsert",
      component: () => import('../views/item/InsertView.vue'),
    },
    {
      path: "/item/get/:ino",
      name: "itemGet",
      component: () => import('../views/item/GetView.vue'),
      props : true,
    },
    {
      path: "/item/update/:ino",
      name: "itemUpdate",
      component: () => import('../views/item/UpdateView.vue'),
      props : true,
    },
    {
      path: "/cart/list",
      name: "cartList",
      component: () => import('../views/cart/ListView.vue'),
      props : true,
    },
    {
      path: "/order/insert",
      name: "orderInsert",
      component: () => import('../views/order/InsertView.vue'),
      // props : true,
    },
    {
      path: "/order/complete/:orderId",
      name: "orderComplete",
      component: () => import('../views/order/CompleteView.vue'),
      props : true,
    },
    {
      path: "/order/list",
      name: "orderList",
      component: () => import('../views/order/OrderList.vue'),
      props : true,
    },
    {
      path: "/order/detail/:orderId",
      name: "orderDetail",
      component: () => import('../views/order/OrderDetail.vue'),
      props : true,
    },
    {
      path: "/order/update",
      name: "orderUpdate",
      component: () => import('../views/order/UpdateView.vue'),
      props : true,
    },
    {
      path: "/admin",
      name: "admin",
      component: () => import('../views/admin/AdminView.vue'),
      props : true,
    },
  ],
});

router.beforeEach((to, from, next) => {
  const store = useStore();
  const { member } = storeToRefs(store);
  const urlArr = [
    '/member/login', 
    '/member/insert', 
    '/board/list', 
    '/board/get', 
    '/reply/list',
    '/item/list', 
    '/item/get',
  ];

  let result = urlArr.filter(i => to.path.includes(i)); 

  if (result.length !== 0 || to.path === "/") {
    next();

  } else if (result.length === 0 && member.value.id === null) {
    next("/member/login");

  } else if (member.value.id !== null) {
    next();
  } 
});

export default router
