import { createRouter, createWebHashHistory } from 'vue-router'
import LayoutView from '../views/LayoutView.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
  },
  {
    path: '/',
    component: LayoutView,
    children: [
      { path: '', name: 'Home', component: () => import('../views/HomeView.vue') },
      { path: 'goods', name: 'GoodsList', component: () => import('../views/GoodsListView.vue') },
      { path: 'lostfound', name: 'LostFound', component: () => import('../views/LostFoundView.vue') },
      { path: 'notice', name: 'Notice', component: () => import('../views/NoticeView.vue') },
      { path: 'item/:id', name: 'ItemDetail', component: () => import('../views/ItemDetail.vue') },
      { path: 'publish', name: 'PublishItem', component: () => import('../views/PublishItem.vue') },
      { path: 'edit/:id', name: 'EditItem', component: () => import('../views/PublishItem.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/UserHomeView.vue') },
      { path: 'my-items', name: 'MyItems', component: () => import('../views/MyItems.vue') },
      { path: 'my-favorites', name: 'MyFavorites', component: () => import('../views/MyFavorites.vue') },
      { path: 'orders', name: 'MyOrders', component: () => import('../views/MyOrders.vue') },
      { path: 'messages', name: 'MyMessages', component: () => import('../views/MyMessages.vue') },
      { path: 'user/:id', name: 'UserHome', component: () => import('../views/PublicUserView.vue') },
      { path: 'stats', name: 'Stats', component: () => import('../views/StatsView.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
