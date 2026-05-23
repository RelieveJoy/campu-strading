<template>
  <div class="publish-container">
    <div class="publish-header">
      <h2>{{ isEdit ? '编辑商品' : '发布商品' }}</h2>
    </div>
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px" class="publish-form">
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title" placeholder="商品标题" maxlength="50" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" placeholder="商品描述" />
      </el-form-item>
      <el-form-item label="售价" prop="price">
        <el-input-number v-model="form.price" :min="0" :precision="2" style="width:200px" />
      </el-form-item>
      <el-form-item label="原价">
        <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:200px" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="选择分类">
          <el-option v-for="c in categories" :key="c.value" :label="c.label" :value="c.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="图片">
        <el-upload :auto-upload="false" :show-file-list="false" :on-change="handleUpload"
          accept="image/*" style="display:inline-block">
          <el-button type="primary">选择图片并上传</el-button>
        </el-upload>
        <p v-if="form.imageUrl" style="margin-top:8px">
          已上传：<a :href="form.imageUrl" target="_blank">查看图片</a>
        </p>
        <img v-if="form.imageUrl" :src="form.imageUrl" style="max-width:300px;display:block;margin-top:8px;border-radius:4px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submit" :loading="loading">{{ isEdit ? '保存修改' : '发布' }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, inject, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addItem, updateItem, getItemDetail, uploadFile } from '../api/item'

const loggedIn = inject('loggedIn')
const requireLogin = inject('requireLogin')

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const isEdit = ref(route.name === 'EditItem')

const categories = [
  { label: '教材', value: 1 }, { label: '电子产品', value: 2 },
  { label: '生活用品', value: 3 }, { label: '衣物', value: 4 }
]

const form = reactive({
  title: '', description: '', price: null, originalPrice: null, categoryId: null, imageUrl: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const handleUpload = async (file) => {
  try {
    const res = await uploadFile(file.raw)
    form.imageUrl = res.data
    ElMessage.success('上传成功')
  } catch (e) { ElMessage.error('上传失败') }
}

const submit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    if (isEdit.value) {
      await updateItem(route.params.id, form)
      ElMessage.success('修改成功')
    } else {
      await addItem(form)
      ElMessage.success('发布成功')
    }
    router.push('/')
  } catch (e) { /* handled */ }
  finally { loading.value = false }
}

const initPage = async () => {
  if (!loggedIn.value) { requireLogin(); return }
  if (isEdit.value) {
    try {
      const res = await getItemDetail(route.params.id)
      Object.assign(form, res.data)
    } catch (e) { ElMessage.error('商品不存在'); router.push('/') }
  }
}

watch(loggedIn, (val) => { if (val) initPage() })

onMounted(initPage)
</script>

<style scoped>
.publish-container { max-width:700px; margin:0 auto; padding:20px; }
.publish-header { display:flex; align-items:center; gap:16px; margin-bottom:24px; }
.publish-header h2 { margin:0; }
.publish-form { background:#fff; padding:24px; border-radius:8px; }
</style>
