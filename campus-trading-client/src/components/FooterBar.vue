<template>
  <footer class="footer-bar">
    <div class="footer-inner">
      <div class="footer-col">
        <h6><i class="bi bi-shop me-1"></i>操场集市</h6>
        <p class="small">操场集市，让闲置物品找到新主人。安全、便捷、绿色。</p>
      </div>
      <div class="footer-col">
        <h6>快捷链接</h6>
        <ul>
          <li><router-link to="/goods">全部商品</router-link></li>
          <li><router-link to="/publish">发布商品</router-link></li>
          <li><router-link to="/lostfound">失物招领</router-link></li>
          <li><router-link to="/orders">我的订单</router-link></li>
          <li><router-link to="/notice">平台使用须知</router-link></li>
        </ul>
      </div>
      <div class="footer-col">
        <h6>平台须知</h6>
        <p class="small mb-1">本平台仅供在校师生使用，请遵守校园规定，诚信交易。</p>
        <router-link to="/notice" class="small">查看完整使用须知</router-link>
      </div>
    </div>
    <div class="footer-bottom">
      <hr />
      <p class="small text-center">&copy; {{ year }} 操场集市 版权所有.</p>
    </div>
  </footer>
</template>

<script setup>
const year = new Date().getFullYear()
</script>

<style scoped>
.footer-bar {
  background: #212529;
  color: rgba(255,255,255,0.5);
  padding: 32px 0 0;
  margin-top: auto;
}
.footer-inner {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: 0 16px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 24px;
}
.footer-col h6 {
  color: #fff;
  font-size: 0.9375rem;
  margin-bottom: 12px;
}
.footer-col ul {
  list-style: none;
  padding: 0;
}
.footer-col li { margin-bottom: 4px; }
.footer-col a {
  color: rgba(255,255,255,0.5);
  text-decoration: none;
  font-size: 0.8125rem;
  transition: color var(--duration-fast);
}
.footer-col a:hover { color: #fff; }
.footer-col .small { font-size: 0.8125rem; line-height: 1.6; }
.footer-bottom {
  max-width: var(--content-max-width);
  margin: 0 auto;
  padding: 0 16px 24px;
}
.footer-bottom hr {
  border-color: rgba(255,255,255,0.1);
  margin: 20px 0;
}
</style>
