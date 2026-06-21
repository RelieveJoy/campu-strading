<template>
  <div class="notice-shell">
    <div class="container">
      <!-- Hero -->
      <section class="notice-hero">
        <span class="notice-kicker">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="16" height="16"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
          Campus Market Guide
        </span>
        <h1 class="notice-title">平台使用须知</h1>
        <p class="notice-subtitle">
          这不是一张简单的规则清单，而是一份面向校园真实交易场景的使用指南。平台只负责信息展示与秩序维护，交易安全、沟通效率和履约体验，仍然取决于买卖双方是否认真、守信、清晰地完成每一步。
        </p>
      </section>

      <!-- Paper -->
      <section class="notice-paper">
        <!-- Summary -->
        <div class="notice-summary">
          <div class="summary-item">
            <strong>先审核后展示</strong>
            <span>所有发布内容均需审核后才能在前台展示。</span>
          </div>
          <div class="summary-item">
            <strong>线下完成交易</strong>
            <span>平台只提供信息发布与撮合，不承担线上收付款功能。</span>
          </div>
          <div class="summary-item">
            <strong>未完结订单上限</strong>
            <span>每位师生最多保留 2 个未完成订单，超出后将限制发布与下单。</span>
          </div>
          <div class="summary-item">
            <strong>违规信息严禁发布</strong>
            <span>诈骗、收费培训、二房东/中介租赁等内容将被拦截或下架。</span>
          </div>
        </div>

        <!-- Flow -->
        <div class="notice-section">
          <div class="section-head">
            <span class="section-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>
            </span>
            <span class="section-title">完整交易流程</span>
          </div>
          <div class="flow-grid">
            <article class="flow-card" v-for="(step, i) in flows" :key="i" :style="{ '--step': `'${String(i+1).padStart(2,'0')}'` }">
              <h3>{{ step.title }}</h3>
              <p>{{ step.desc }}</p>
            </article>
          </div>
        </div>

        <!-- Rules + Risks -->
        <div class="notice-two-col">
          <section class="notice-card">
            <div class="section-head">
              <span class="section-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              </span>
              <span class="section-title">交易规则与平台责任</span>
            </div>
            <div class="rule-list">
              <div class="rule-item" v-for="rule in rules" :key="rule.title">
                <span class="rule-icon">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="18" height="18"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg>
                </span>
                <div>
                  <strong>{{ rule.title }}</strong>
                  <p>{{ rule.desc }}</p>
                </div>
              </div>
            </div>
          </section>

          <aside class="notice-card">
            <div class="section-head">
              <span class="section-icon danger-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="20" height="20"><circle cx="12" cy="12" r="10"/><line x1="4.93" y1="4.93" x2="19.07" y2="19.07"/></svg>
              </span>
              <span class="section-title">严禁发布内容</span>
            </div>
            <div class="risk-list">
              <div class="risk-item" v-for="risk in risks" :key="risk.title">
                <h4>{{ risk.title }}</h4>
                <p>{{ risk.desc }}</p>
              </div>
            </div>
          </aside>
        </div>

        <!-- Note -->
        <section class="notice-note">
          <strong>补充提醒</strong>
          <div>建议买家优先选择校内公共区域当面交易，并在见面前再次核对商品版本、附件是否齐全、价格是否一致。</div>
          <div>建议卖家上传真实清晰的图片，准确说明瑕疵、使用情况与交易条件，避免因描述不清引发争议。</div>
          <div>若遇到恶意爽约、疑似诈骗、重复违规发布等情况，请使用平台举报功能，平台会根据记录进行处理。</div>
          <div class="mt-3">如需返回首页，可前往 <router-link to="/">操场集市首页</router-link>；如需浏览商品，可前往 <router-link to="/goods">全部商品</router-link>。</div>
        </section>
      </section>
    </div>
  </div>
</template>

<script setup>
const flows = [
  { title: '浏览商品', desc: '通过分类、搜索或推荐入口查看商品详情，优先确认图片、价格、成色、交易地点与卖家信息。' },
  { title: '在线咨询', desc: '下单前先沟通库存、商品状态、交易时间与方式，避免"拍下后再问"的低效交易。' },
  { title: '提交订单', desc: '确认购买意向后提交订单。订单生成后，该商品会暂时下架，请尽快完成后续沟通与履约。' },
  { title: '卖家发货/交付', desc: '根据双方约定完成线下当面交付或其他约定方式，卖家需及时响应，避免长时间无进展。' },
  { title: '买家收货', desc: '买家在核对商品与描述一致后确认收货；如有问题，应第一时间与卖家协商处理。' },
  { title: '订单完成', desc: '双方完成交易后及时结束订单，让账号恢复正常交易额度，也为平台秩序留下清晰记录。' },
]

const rules = [
  { title: '发布信息需先审核', desc: '商品发布后，需经管理员审核确认合法合规，审核通过后才会在前台展示。' },
  { title: '下单后商品自动下架', desc: '为避免重复购买或库存冲突，商品一旦被下单会暂时下架，请买卖双方尽快推进交易流程。' },
  { title: '未完成订单不得长期积压', desc: '每位师生最多允许保留 2 个未完成订单，超过后将无法继续发布商品或下单，直至先前订单处理完成。' },
  { title: '平台不直接参与钱货结算', desc: '本平台只提供商品信息发布与沟通服务，实际付款、验货、交付由买卖双方自行协商，并在线下完成。' },
]

const risks = [
  { title: '虚假或诈骗信息', desc: '包括伪造商品、虚构交易、诱导转账、低价引流、冒充师生身份等内容，发现即处理。' },
  { title: '收费性质培训信息', desc: '平台定位为校园二手物品流转场景，不接受课程售卖、付费辅导、陪练代练等收费培训发布。' },
  { title: '二房东/中介租赁信息', desc: '平台禁止以二房东、代理、中介身份发布房屋租赁信息，避免校内信息不透明带来的纠纷风险。' },
  { title: '其他违规信息', desc: '包括违禁品、侵权商品、刷单引流、批量商用倒卖、侮辱性或明显不实内容等。' },
]
</script>

<style scoped>
.notice-shell {
  background:
    radial-gradient(circle at top left, rgba(32, 65, 152, 0.08), transparent 28%),
    radial-gradient(circle at top right, rgba(174, 186, 217, 0.1), transparent 30%),
    linear-gradient(180deg, #f4f6fb 0%, #f8f6ee 52%, #f3efe3 100%);
  padding: var(--space-xxl) 0;
  min-height: 100vh;
}
.container { max-width: 960px; margin: 0 auto; padding: 0 16px; }

/* ── Hero ── */
.notice-hero {
  position: relative; overflow: hidden;
  border-radius: 24px; padding: 2.5rem;
  background:
    linear-gradient(140deg, rgba(32, 65, 152, 0.96), rgba(20, 41, 96, 0.94)),
    linear-gradient(120deg, rgba(174, 186, 217, 0.15), transparent 40%);
  color: #e8ecf8;
  box-shadow: 0 24px 60px rgba(32, 65, 152, 0.18);
  margin-bottom: -2rem;
}
.notice-hero::after {
  content: ''; position: absolute; right: -80px; top: -70px;
  width: 240px; height: 240px; border-radius: 32px; transform: rotate(18deg);
  background: linear-gradient(180deg, rgba(174, 186, 217, 0.34), rgba(174, 186, 217, 0));
}
.notice-kicker {
  display: inline-flex; align-items: center; gap: 8px;
  padding: 6px 14px; border-radius: 999px;
  background: rgba(255,255,255,0.1); color: #c8d3f0;
  font-size: 0.75rem; font-weight: 600; letter-spacing: 0.06em;
  text-transform: uppercase;
}
.notice-title {
  position: relative; z-index: 1;
  margin: 1rem 0 0.6rem;
  font-size: clamp(1.8rem, 3vw, 3rem);
  font-weight: 800; line-height: 1.15; letter-spacing: 0.01em;
}
.notice-subtitle {
  position: relative; z-index: 1;
  max-width: 680px; margin: 0;
  color: rgba(232, 236, 248, 0.85);
  font-size: 0.9375rem; line-height: 1.7;
}

/* ── Paper ── */
.notice-paper {
  position: relative; padding: 2rem;
  border-radius: 20px;
  background: linear-gradient(180deg, rgba(255,255,255,.95), rgba(251,250,244,.98));
  border: 1px solid rgba(255,255,255,0.85);
  box-shadow: 0 14px 40px rgba(32, 65, 152, 0.06);
}

/* ── Summary ── */
.notice-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 1rem; margin-bottom: 2rem;
}
.summary-item {
  padding: 1rem; border-radius: 14px;
  background: #f0f3fb; border: 1px solid rgba(32, 65, 152, 0.06);
}
.summary-item strong { display: block; color: var(--color-primary); font-size: 1rem; margin-bottom: 4px; }
.summary-item span { font-size: 0.8125rem; color: #5a647a; line-height: 1.5; }

/* ── Section ── */
.notice-section { margin-bottom: 2rem; }
.section-head { display: flex; align-items: center; gap: 10px; margin-bottom: 1rem; }
.section-icon {
  width: 38px; height: 38px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  background: rgba(32, 65, 152, 0.08); color: var(--color-primary);
}
.danger-icon { background: rgba(220, 53, 69, 0.08); color: var(--color-danger); }
.section-title { font-size: 1.1rem; font-weight: 700; color: var(--color-ink); }

/* ── Flow Grid ── */
.flow-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}
.flow-card {
  position: relative; min-height: 150px; padding: 1rem;
  border-radius: 18px; background: #fbfaf6;
  border: 1px solid rgba(32, 65, 152, 0.1);
}
.flow-card::before {
  content: var(--step); position: absolute; right: 1rem; top: 1rem;
  font-size: 1.8rem; font-weight: 800;
  color: rgba(32, 65, 152, 0.06);
}
.flow-card h3 { margin-bottom: 0.5rem; color: var(--color-primary); font-size: 1rem; font-weight: 700; }
.flow-card p { margin: 0; color: #5a647a; font-size: 0.8125rem; line-height: 1.7; }

/* ── Two Col ── */
.notice-two-col {
  display: grid;
  grid-template-columns: 1.25fr 0.95fr;
  gap: 1.25rem; margin-bottom: 1.5rem;
}
.notice-card {
  padding: 1.5rem; border-radius: 18px;
  background: rgba(255,255,255,0.75);
  border: 1px solid rgba(32, 65, 152, 0.08);
}

/* ── Rule List ── */
.rule-list { display: grid; gap: 0.8rem; }
.rule-item { display: flex; align-items: flex-start; gap: 0.8rem; padding-bottom: 0.8rem; border-bottom: 1px dashed rgba(32, 65, 152, 0.1); }
.rule-item:last-child { border-bottom: 0; padding-bottom: 0; }
.rule-icon {
  flex-shrink: 0; width: 36px; height: 36px; border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  background: rgba(32, 65, 152, 0.06); color: var(--color-primary);
}
.rule-item strong { display: block; margin-bottom: 2px; color: var(--color-ink); font-size: 0.9375rem; }
.rule-item p { margin: 0; color: #5a647a; font-size: 0.8125rem; line-height: 1.6; }

/* ── Risk List ── */
.risk-list { display: grid; gap: 0.8rem; }
.risk-item {
  padding: 0.9rem 1rem; border-radius: 14px;
  background: linear-gradient(180deg, rgba(255,255,255,0.9), rgba(248,244,234,0.95));
  border-left: 3px solid #dc3545;
}
.risk-item h4 { margin-bottom: 4px; color: #842029; font-size: 0.9375rem; font-weight: 700; }
.risk-item p { margin: 0; color: #6a5a5a; font-size: 0.8125rem; line-height: 1.6; }

/* ── Note ── */
.notice-note {
  padding: 1.3rem 1.5rem; border-radius: 18px;
  background: linear-gradient(135deg, #eff2fb, #f4fbef);
  border: 1px solid rgba(32, 65, 152, 0.12);
  color: #4a5470; line-height: 1.8; font-size: 0.875rem;
}
.notice-note strong { display: block; margin-bottom: 8px; color: var(--color-primary); font-size: 1rem; }
.notice-note div { margin-bottom: 4px; }

@media (max-width: 768px) {
  .notice-hero { padding: 1.5rem; border-radius: 18px; }
  .notice-paper { padding: 1.25rem; }
  .notice-two-col { grid-template-columns: 1fr; }
  .flow-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
