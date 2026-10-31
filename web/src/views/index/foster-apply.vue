<template>
  <div class="foster-page">
    <Header />
    <div class="content">
      <div class="page-title">
        <h2>宠物寄养</h2>
        <p>为您的爱宠找到温馨的临时家园</p>
      </div>

      <!-- 寄养申请表单 -->
      <div class="form-card">
        <h3 style="margin-bottom: 20px;">提交寄养申请</h3>
        <a-form :label-col="{ style: { width: '100px' } }" :model="form" :rules="rules" ref="myform">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="宠物名称" name="petName">
                <a-input v-model:value="form.petName" placeholder="请输入宠物名称" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="寄养天数" name="duration">
                <a-input-number v-model:value="form.duration" :min="1" placeholder="请输入寄养天数" style="width: 100%;" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="入住日期" name="startDate">
                <a-date-picker v-model:value="form.startDate" value-format="YYYY-MM-DD" style="width: 100%;" placeholder="请选择入住日期" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="宠物体重" name="petWeight">
                <a-input-number v-model:value="form.petWeight" :min="0.1" :step="0.5" placeholder="请输入体重(kg)" style="width: 100%;" />
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="宠物照片" :label-col="{ style: { width: '100px' } }">
                <a-upload-dragger
                    name="file"
                    accept="image/*"
                    :multiple="false"
                    :before-upload="beforeUpload"
                    :file-list="fileList"
                    @remove="handleRemove"
                >
                  <p class="ant-upload-drag-icon">
                    <template v-if="form.photoUrl">
                      <img :src="form.photoUrl" style="width: 60px; height: 60px; object-fit: cover;" />
                    </template>
                    <template v-else>
                      <file-image-outlined />
                    </template>
                  </p>
                  <p class="ant-upload-text">请上传爱宠近照，便于工作人员审核与照护</p>
                </a-upload-dragger>
              </a-form-item>
            </a-col>
            <a-col :span="24">
              <a-form-item label="寄养备注" :label-col="{ style: { width: '100px' } }">
                <a-textarea v-model:value="form.remark" :rows="3" :maxlength="500" placeholder="可填写病史、饮食禁忌、特殊照护需求等（选填）" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item>
                <a-button type="primary" @click="handleSubmit" :loading="submitting" style="margin-top: 4px;">提交寄养申请</a-button>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 我的寄养记录 -->
      <div class="form-card" style="margin-top: 24px;">
        <h3 style="margin-bottom: 20px;">我的寄养记录</h3>
        <a-table
            size="middle"
            rowKey="orderId"
            :loading="listLoading"
            :columns="columns"
            :data-source="fosterList"
            :pagination="{ pageSize: 5, showTotal: (total) => `共${total}条` }"
        >
          <template #bodyCell="{ text, column }">
            <template v-if="column.key === 'progress'">
              <a-tag :color="getProgressColor(text)">{{ progressText[Number(text)] || text }}</a-tag>
            </template>
            <template v-if="column.key === 'pet_weight'">
              {{ text ? text + ' kg' : '-' }}
            </template>
          </template>
        </a-table>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import {createApi, listApi} from '/@/api/foster';
import {FormInstance, message} from 'ant-design-vue';
import {FileImageOutlined} from '@ant-design/icons-vue';

const router = useRouter();
const myform = ref<FormInstance>();
const submitting = ref(false);
const listLoading = ref(false);
const fosterList = ref([]);
const userId = localStorage.getItem('user_id');
const fileList = ref<any[]>([]);

const form = reactive({petName: '', duration: undefined, startDate: '', petWeight: undefined, remark: '', photoUrl: undefined, imageFile: undefined});
const rules = {
  petName: [{required: true, message: '请输入宠物名称', trigger: 'change'}],
  duration: [{required: true, message: '请输入寄养天数', trigger: 'change'}],
  startDate: [{required: true, message: '请选择入住日期', trigger: 'change'}],
  petWeight: [{required: true, message: '请输入宠物体重', trigger: 'change'}],
};

// 照片仅本地预览，随表单 FormData 一并提交到后端上传
const beforeUpload = (file: File) => {
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  form.imageFile = new File([file], fileName);
  const reader = new FileReader();
  reader.onload = (e) => { form.photoUrl = e.target?.result as string; };
  reader.readAsDataURL(file);
  fileList.value = [file];
  return false;
};

const handleRemove = () => {
  form.imageFile = undefined;
  form.photoUrl = undefined;
  fileList.value = [];
};

const columns = [
  {title: '宠物名', dataIndex: 'pet_name', key: 'pet_name', align: 'center'},
  {title: '体重', dataIndex: 'pet_weight', key: 'pet_weight', align: 'center'},
  {title: '入住日期', dataIndex: 'start_date', key: 'start_date', align: 'center'},
  {title: '寄养天数', dataIndex: 'duration', key: 'duration', align: 'center'},
  {title: '房间编号', dataIndex: 'room_name', key: 'room_name', align: 'center', customRender: ({text}) => text || '-'},
  {title: '状态', dataIndex: 'progress', key: 'progress', align: 'center'},
];

// 寄养进度：1-待处理，2-寄养中，3-已完成，4-已领回，5-已预约
const progressText: Record<number, string> = {1: '待处理', 2: '寄养中', 3: '已完成', 4: '已领回', 5: '已预约'};

const getProgressColor = (progress) => {
  return {4: 'green', 3: 'green', 2: 'blue', 5: 'orange', 1: 'red'}[Number(progress)] || 'default';
};

const getMyFosterList = () => {
  listLoading.value = true;
  listApi({}).then((res) => {
    listLoading.value = false;
    fosterList.value = (res.data || []).filter(item => String(item.client_uid) === String(userId));
  }).catch(() => { listLoading.value = false; });
};

const handleSubmit = () => {
  if (!userId) {
    message.warn('请先登录');
    router.push({name: 'login'});
    return;
  }
  myform.value?.validate().then(() => {
    submitting.value = true;
    const formData = new FormData();
    formData.append('clientUid', String(parseInt(userId)));
    formData.append('petName', form.petName);
    formData.append('startDate', form.startDate);
    formData.append('duration', String(form.duration));
    formData.append('petWeight', form.petWeight != null ? String(form.petWeight) : '');
    formData.append('remark', form.remark || '');
    formData.append('progress', '1'); // 1-待处理
    if (form.imageFile) {
      formData.append('imageFile', form.imageFile);
    }
    createApi(formData).then(() => {
      submitting.value = false;
      message.success('寄养申请已提交');
      form.petName = '';
      form.duration = undefined;
      form.startDate = '';
      form.petWeight = undefined;
      form.remark = '';
      handleRemove();
      getMyFosterList();
    }).catch(() => {
      submitting.value = false;
      message.error('提交失败');
    });
  }).catch(() => { message.warn('请填写完整信息'); });
};

onMounted(() => { getMyFosterList(); });
</script>

<style scoped lang="less">
.foster-page { min-height: 100vh; background: #f0f2f5; }
.content { max-width: 900px; margin: 0 auto; padding: 80px 24px 40px; }
.page-title { text-align: center; margin-bottom: 32px;
  h2 { font-size: 28px; color: #333; margin-bottom: 8px; }
  p { color: #999; font-size: 14px; }
}
.form-card { background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
</style>