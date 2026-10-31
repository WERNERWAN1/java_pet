<template>
  <div class="submit-page">
    <Header />
    <div class="content">
      <div class="page-title">
        <h2>流浪动物上报</h2>
        <p>发现流浪动物？请在这里上报，我们会及时处理</p>
      </div>

      <!-- 上报表单 -->
      <div class="form-card">
        <h3 style="margin-bottom: 20px;">提交上报信息</h3>
        <a-form :label-col="{ style: { width: '100px' } }" :model="form" :rules="rules" ref="myform">
          <a-form-item label="情况说明" name="name">
            <a-textarea v-model:value="form.name" placeholder="请描述流浪动物的情况（位置、数量、状态等）" :rows="4" />
          </a-form-item>
          <a-form-item label="上传图片">
            <a-upload-dragger
                name="file"
                accept="image/*"
                :multiple="false"
                :before-upload="beforeUpload"
                :file-list="fileList"
                @remove="handleRemove"
            >
              <p class="ant-upload-drag-icon">
                <template v-if="form.imgUrl">
                  <img :src="form.imgUrl" style="width: 80px; height: 80px; object-fit: cover; border-radius: 4px;" />
                </template>
                <template v-else>
                  <file-image-outlined />
                </template>
              </p>
              <p class="ant-upload-text">点击或拖拽图片到此区域上传</p>
            </a-upload-dragger>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" size="large" @click="handleSubmit" :loading="submitting">提交上报</a-button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 我的上报记录 -->
      <div class="form-card" style="margin-top: 24px;">
        <h3 style="margin-bottom: 20px;">我的上报记录</h3>
        <a-table
            size="middle"
            rowKey="id"
            :loading="listLoading"
            :columns="columns"
            :data-source="reportList"
            :pagination="{ pageSize: 5, showTotal: (total) => `共${total}条` }"
        >
          <template #bodyCell="{ text, record, column }">
            <template v-if="column.key === 'img'">
              <img v-if="text" :src="getImgUrl(text)" style="width: 50px; height: 50px; object-fit: cover; border-radius: 4px;" />
              <span v-else style="color: #ccc;">无</span>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="Number(text) === 2 ? 'green' : 'orange'">{{ submitStatusText[Number(text)] || text }}</a-tag>
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
import {createApi, listApi} from '/@/api/submit';
import {FormInstance, message} from 'ant-design-vue';
import {FileImageOutlined} from '@ant-design/icons-vue';
import {BASE_URL} from "/@/store/constants";

const router = useRouter();
const myform = ref<FormInstance>();
const submitting = ref(false);
const listLoading = ref(false);
const reportList = ref([]);
const fileList = ref([]);

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

// 处理状态：1-待处理，2-已处理
const submitStatusText = {1: '待处理', 2: '已处理'};

const form = reactive({name: '', imageFile: null, imgUrl: ''});
const rules = {name: [{required: true, message: '请输入情况说明', trigger: 'change'}]};

const beforeUpload = (file) => {
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  form.imageFile = copyFile;
  const reader = new FileReader();
  reader.onload = (e) => { form.imgUrl = e.target.result; };
  reader.readAsDataURL(file);
  fileList.value = [file];
  return false;
};

const handleRemove = () => {
  form.imageFile = null;
  form.imgUrl = '';
  fileList.value = [];
};

const columns = [
  {title: '说明', dataIndex: 'name', key: 'name', align: 'center', ellipsis: true},
  {title: '图片', dataIndex: 'img', key: 'img', align: 'center', width: 80},
  {title: '上报时间', dataIndex: 'time', key: 'time', align: 'center'},
  {title: '状态', dataIndex: 'status', key: 'status', align: 'center'},
];

const getMyReports = () => {
  listLoading.value = true;
  listApi({}).then((res) => {
    listLoading.value = false;
    // 按时间倒序
    reportList.value = (res.data || []).reverse();
  }).catch(() => { listLoading.value = false; });
};

const handleSubmit = () => {
  myform.value?.validate().then(() => {
    submitting.value = true;
    const formData = new FormData();
    formData.append('name', form.name);
    formData.append('status', '1'); // 1-待处理（上报时间由服务端生成，避免客户端时间格式/时区问题）
    if (form.imageFile) {
      formData.append('imageFile', form.imageFile);
    }
    createApi(formData).then(() => {
      submitting.value = false;
      message.success('上报成功，管理员会尽快处理');
      form.name = '';
      form.imageFile = null;
      form.imgUrl = '';
      fileList.value = [];
      getMyReports();
    }).catch(() => {
      submitting.value = false;
      message.error('提交失败');
    });
  }).catch(() => { message.warn('请填写必要信息'); });
};

onMounted(() => { getMyReports(); });
</script>

<style scoped lang="less">
.submit-page { min-height: 100vh; background: #f0f2f5; }
.content { max-width: 900px; margin: 0 auto; padding: 80px 24px 40px; }
.page-title { text-align: center; margin-bottom: 32px;
  h2 { font-size: 28px; color: #333; margin-bottom: 8px; }
  p { color: #999; font-size: 14px; }
}
.form-card { background: #fff; padding: 24px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
</style>