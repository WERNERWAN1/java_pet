<template>
  <div class="foster-page">
    <Header />
    <div class="content">
      <div class="page-title">
        <h2>宠物寄养</h2>
        <p>为您的爱宠找到温馨的临时家园</p>
      </div>
      <div class="form-card">
        <a-form :label-col="{ style: { width: '100px' } }" :model="form" :rules="rules" ref="myform">
          <a-form-item label="宠物名称" name="petName">
            <a-input v-model:value="form.petName" placeholder="请输入宠物名称" />
          </a-form-item>
          <a-form-item label="寄养天数" name="duration">
            <a-input-number v-model:value="form.duration" :min="1" placeholder="请输入寄养天数" style="width: 200px;" />
          </a-form-item>
          <a-form-item label="入住日期" name="startDate">
            <a-input v-model:value="form.startDate" placeholder="请输入入住日期，如 2024-01-01" />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" size="large" @click="handleSubmit" :loading="submitting">提交寄养申请</a-button>
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
import {createApi} from '/@/api/foster';
import {FormInstance, message} from 'ant-design-vue';

const router = useRouter();
const myform = ref<FormInstance>();
const submitting = ref(false);
const form = reactive({petName: '', duration: undefined, startDate: ''});
const rules = {
  petName: [{required: true, message: '请输入宠物名称', trigger: 'change'}],
  duration: [{required: true, message: '请输入寄养天数', trigger: 'change'}],
  startDate: [{required: true, message: '请输入入住日期', trigger: 'change'}],
};

const handleSubmit = () => {
  myform.value?.validate().then(() => {
    const userId = localStorage.getItem('user_id');
    if (!userId) {
      message.warn('请先登录');
      router.push({name: 'login'});
      return;
    }
    submitting.value = true;
    createApi({
      clientUid: parseInt(userId),
      petName: form.petName,
      startDate: form.startDate,
      duration: form.duration,
      progress: '待处理',
    }).then(() => {
      submitting.value = false;
      message.success('寄养申请已提交');
      router.push({name: 'myFoster'});
    }).catch(() => {
      submitting.value = false;
      message.error('提交失败');
    });
  }).catch(() => { message.warn('请填写完整信息'); });
};
</script>

<style scoped lang="less">
.foster-page { min-height: 100vh; }
.content { max-width: 600px; margin: 0 auto; padding: 40px 24px; }
.page-title { text-align: center; margin-bottom: 32px;
  h2 { font-size: 28px; color: #333; margin-bottom: 8px; }
  p { color: #999; font-size: 14px; }
}
.form-card { background: #fff; padding: 32px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
</style>