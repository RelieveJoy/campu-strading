<template>
  <el-dialog v-model="visible" title="举报" width="450px" :close-on-click-modal="false" @closed="reset">
    <el-form :model="form" label-width="80px">
      <el-form-item label="举报类型">
        <el-radio-group v-model="form.targetType" disabled>
          <el-radio :value="targetType">{{ targetType === 'item' ? '商品' : targetType === 'user' ? '用户' : '失物招领' }}</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="举报原因" required>
        <el-select v-model="form.reason" placeholder="请选择举报原因" style="width:100%">
          <el-option label="虚假信息" value="虚假信息" />
          <el-option label="违规内容" value="违规内容" />
          <el-option label="欺诈行为" value="欺诈行为" />
          <el-option label="侵犯隐私" value="侵犯隐私" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="详细描述">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请描述具体情况（选填）" maxlength="500" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="danger" @click="submit" :loading="submitting">提交举报</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { submitReport } from '../api/report'

const props = defineProps({
  targetType: { type: String, default: 'item' },  // item | user | lostfound
  targetId: { type: Number, required: true },
})

const visible = ref(false)
const submitting = ref(false)
const form = reactive({
  targetType: props.targetType,
  targetId: props.targetId,
  reason: '',
  description: '',
})

function open() { visible.value = true }
function reset() {
  form.reason = ''
  form.description = ''
}

async function submit() {
  if (!form.reason) {
    ElMessage.warning('请选择举报原因')
    return
  }
  submitting.value = true
  try {
    await submitReport({
      targetType: props.targetType,
      targetId: props.targetId,
      reason: form.reason,
      description: form.description,
    })
    ElMessage.success('举报已提交，我们会尽快处理')
    visible.value = false
  } catch { /* interceptor handles */ }
  finally { submitting.value = false }
}

defineExpose({ open })
</script>
