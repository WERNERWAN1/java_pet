<template>
  <div class="animal-detail-page">
    <Header />
    <div class="content">
      <a-spin :spinning="loading">
        <div class="detail-card" v-if="animal">
          <div class="detail-left">
            <img v-if="animal.photo" :src="getImgUrl(animal.photo)" class="detail-img" />
            <div v-else class="no-img">暂无图片</div>
          </div>
          <div class="detail-right">
            <h2>{{ animal.nickname }}</h2>
            <div class="info-row"><span class="label">品种：</span>{{ animal.breed }}</div>
            <div class="info-row"><span class="label">性别：</span>{{ animal.gender }}</div>
            <div class="info-row"><span class="label">年龄：</span>{{ animal.years }}岁</div>
            <div class="info-row"><span class="label">健康状态：</span>{{ animal.healthCondition }}</div>
            <div class="info-row desc"><span class="label">介绍：</span>{{ animal.introduction }}</div>
            <a-button type="primary" size="large" @click="handleAdopt" style="margin-top: 24px;">申请领养</a-button>
          </div>
        </div>
      </a-spin>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import {detailApi} from '/@/api/adoptablePet';
import {createApi} from '/@/api/adoptionRecord';
import {BASE_URL} from "/@/store/constants";
import {message} from 'ant-design-vue';

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const animal = ref(null);

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

const handleAdopt = () => {
  const userId = localStorage.getItem('user_id');
  if (!userId) {
    message.warn('请先登录');
    router.push({name: 'login'});
    return;
  }
  createApi({
    ownerUid: parseInt(userId),
    petUid: animal.value.pid,
    adoptDate: new Date().toISOString().split('T')[0],
    state: '待审核'
  }).then(() => {
    message.success('领养申请已提交');
    router.push({name: 'myAdopt'});
  }).catch(() => {
    message.error('申请失败');
  });
};

onMounted(() => {
  const pid = route.query.pid;
  if (pid) {
    loading.value = true;
    detailApi({pid}).then((res) => {
      loading.value = false;
      animal.value = res.data;
    }).catch(() => { loading.value = false; });
  }
});
</script>

<style scoped lang="less">
.animal-detail-page { min-height: 100vh; }
.content { max-width: 1000px; margin: 0 auto; padding: 80px 24px 40px; }
.detail-card { display: flex; gap: 40px; background: #fff; padding: 32px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.detail-left { width: 400px; flex-shrink: 0;
  .detail-img { width: 100%; height: 400px; object-fit: cover; border-radius: 8px; }
  .no-img { width: 100%; height: 400px; background: #f5f5f5; display: flex; align-items: center; justify-content: center; color: #999; border-radius: 8px; }
}
.detail-right { flex: 1;
  h2 { font-size: 28px; color: #333; margin-bottom: 24px; }
  .info-row { font-size: 16px; color: #666; margin-bottom: 12px;
    .label { color: #999; }
  }
  .desc { line-height: 1.8; }
}
</style>