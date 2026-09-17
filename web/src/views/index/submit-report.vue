<template>
  <div class="submit-page">
    <Header />
    <div class="content">
      <div class="page-title">
        <h2>流浪动物上报</h2>
        <p>发现流浪动物？请在这里上报，我们会及时处理</p>
      </div>
      <div class="form-card">
        <a-form :label-col="{ style: { width: '100px' } }" :model="form" :rules="rules" ref="myform">
          <a-form-item label="情况说明" name="name">
            <a-textarea v-model:value="form.name" placeholder="请描述流浪动物的情况（位置、数量、状态等）" :rows="4" />
          </a-form-item>
          <a-form-item label="上报图片" name="img">
            <a-input v-model:value="form.img" placeholder="请输入图片路径" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" size="large" @click="handleSubmit" :loading="submitting">提交上报</a-button>
          </a-form-item>
        </a-form>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import {createApi} from '/@/api/submit';
import {FormInstance, message} from 'ant-design-vue';

const router = useRouter();
const myform = ref<FormInstance>();
const submitting = ref(false);
const form = reactive({name: '', img: ''});
const rules = {name: [{required: true, message: '请输入情况说明', trigger: 'change'}]};

const handleSubmit = () => {
  myform.value?.validate().then(() => {
    submitting.value = true;
    createApi({
      name: form.name,
      img: form.img,
      time: new Date().toLocaleString(),
      status: '待处理',
    }).then(() => {
      submitting.value = false;
      message.success('上报成功，管理员会尽快处理');
      router.push({name: 'portal'});
    }).catch(() => {
      submitting.value = false;
      message.error('提交失败');
    });
  }).catch(() => { message.warn('请填写必要信息'); });
};
</script>

<style scoped lang="less">
.submit-page { min-height: 100vh; }
.content { max-width: 600px; margin: 0 auto; padding: 40px 24px; }
.page-title { text-align: center; margin-bottom: 32px;
  h2 { font-size: 28px; color: #333; margin-bottom: 8px; }
  p { color: #999; font-size: 14px; }
}
.form-card { background: #fff; padding: 32px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
</style>