<script setup lang="ts">
import type { UnwrapRef } from 'vue'
import { ref, reactive, computed, toRaw } from 'vue'
import { UploadOutlined } from '@ant-design/icons-vue'
import { message, notification } from 'ant-design-vue' // 引入提示组件
import { useUserStore } from '@/stores/user' // 假设你的 store 路径
import { useI18n } from 'vue-i18n'
import { updateUserInfo } from '~@/api/common/user'
import { uploadFile } from '@/api/upload' // 确保引入上传接口

interface FormState {
  username: string
  email: string 
  nickname: string
  intro: string
  avatar: string
}

const userStore = useUserStore()
const { t } = useI18n()

const formRef = ref()
const labelCol = { span: 0 }
const wrapperCol = { span: 13 }

// 初始化表单数据
const formState: UnwrapRef<FormState> = reactive({
  username: userStore.userInfo?.username || '',
  email: userStore.userInfo?.email || '',
  nickname: userStore.userInfo?.nickname || '', 
  intro: userStore.userInfo?.intro || '',
  avatar: userStore.userInfo?.avatar || '',
})

// 表单验证规则
const rules: any = computed(() => {
  return {
    nickname: [{ required: true, message: t('account.settings.form-rule-name'), trigger: 'change' }],
    email: [{ required: true, message: t('account.settings.form-rule-email'), trigger: 'change' }],
    // 其他规则可按需保留...
  }
})

/**
 * 自定义上传方法
 * Ant Design Vue 的 Upload 组件支持 customRequest
 */
const customUploadRequest = async (options: any) => {
  const { file, onSuccess, onError } = options
  
  try {
    // 1. 调用上传接口
    const res = await uploadFile(file) as any // 根据实际响应类型断言
    
    if (res.code === 200) {
      // 2. 上传成功，拿到图片 URL (假设后端返回在 res.data 中，如果是 res.data.url 请自行调整)
      const imgUrl = res.data 
      
      // 3. 更新表单中的头像字段，实现预览
      formState.avatar = imgUrl
      
      // 4. 通知组件上传成功
      onSuccess(res, file)
      
      // 5. 【核心需求】右上角提示需要点击保存
      notification.info({
        message: '头像已上传',
        description: '图片上传成功，请点击下方的“更新基本信息”按钮以保存修改。',
        duration: 4, // 4秒后自动关闭
        placement: 'topRight',
      })
    } else {
      message.error(res.msg || '上传失败')
      onError(new Error('Upload failed'))
    }
  } catch (error) {
    console.error(error)
    message.error('网络错误，上传失败')
    onError(error)
  }
}

// 提交表单（保存修改）
async function onSubmit() {
  try {
    // 1. 校验表单
    await formRef.value.validate()
    
    // 2. 发送请求
    const params = toRaw(formState)
    // 如果后端字段不一致，可以在这里转换，例如： params.username = params.name
    
    const res = await updateUserInfo(params) as any
    
    if (res.code === 200) {
      message.success('修改个人信息成功')
      
      // 3. 选做：更新 Store 中的用户信息，让全局头像立即变化
      // userStore.setUserInfo({ ...userStore.userInfo, ...params }) 
      // 或者重新拉取一次用户信息 userStore.getUserInfo()
    } else {
      message.error(res.msg || '修改失败')
    }
  } catch (error) {
    console.log('error', error)
  }
}
</script>

<template>
  <a-card :title="t('account.settings.basic-setting')" :bordered="false">
    <a-row>
      <a-col :span="12">
        <a-form
          ref="formRef"
          :model="formState"
          :rules="rules"
          :label-col="labelCol"
          :wrapper-col="wrapperCol"
        >
          <a-form-item :label-col="{ span: 24 }" :label="t('account.settings.form-email')" name="email">
            <a-input v-model:value="formState.email" :placeholder="t('account.settings.form-input-plac')" style="width: 320px;" />
          </a-form-item>
          
          <a-form-item :label-col="{ span: 24 }" :label="t('account.settings.form-name')" name="name">
            <a-input v-model:value="formState.nickname" :placeholder="t('account.settings.form-input-plac')" style="width: 320px;" />
          </a-form-item>
          
          <a-form-item name="desc" :label="t('account.settings.form-desc')" :label-col="{ span: 24 }">
            <a-textarea v-model:value="formState.intro" :placeholder="t('account.settings.form-input-plac')" :rows="4" />
          </a-form-item>
          
          <a-form-item>
            <a-button type="primary" @click="onSubmit">
              {{ t('account.settings.form-submit') }}
            </a-button>
          </a-form-item>
        </a-form>
      </a-col>
      
      <a-col :span="4">
        <p>
          {{ t('account.settings.basic-avatar') }}
        </p>
        <div class="flex flex-col items-center">
          <a-avatar :size="100" class="mb-4">
            <template #icon>
              <img v-if="formState.avatar" :src="formState.avatar" alt="avatar" style="object-fit: cover; width: 100%; height: 100%;" />
              <UserOutlined v-else />
            </template>
          </a-avatar>
          
          <a-upload
            name="file"
            :show-upload-list="false"
            :custom-request="customUploadRequest"
            accept="image/*"
          >
            <a-button>
              <UploadOutlined />
              {{ t('account.settings.basic-avatar.upload') }}
            </a-button>
          </a-upload>
        </div>
      </a-col>
    </a-row>
  </a-card>
</template>

<style scoped>
/* 简单的样式补充 */
.mb-4 {
  margin-bottom: 1rem;
}
</style>
