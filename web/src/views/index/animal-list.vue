<template>
  <div class="animal-list-page">
    <Header />
    <div class="content">
      <div class="page-title">
        <h2>宠物领养</h2>
        <p>在这里找到你的小伙伴</p>
      </div>
      <a-spin :spinning="loading" style="min-height: 200px;">
        <div class="card-list">
          <div v-for="item in animalList" :key="item.pid" class="card-item" @click="goDetail(item.pid)">
            <div class="card-img">
              <img v-if="item.photo" :src="getImgUrl(item.photo)" />
              <div v-else class="no-img">暂无图片</div>
            </div>
            <div class="card-info">
              <h3>{{ item.nickname }}</h3>
              <p><span class="label">品种：</span>{{ item.breed }}</p>
              <p><span class="label">性别：</span>{{ item.gender }}</p>
              <p><span class="label">年龄：</span>{{ item.years }}岁</p>
              <p><span class="label">状态：</span>{{ item.healthCondition }}</p>
            </div>
          </div>
          <div v-if="animalList.length === 0 && !loading" class="no-data">暂无可领养宠物</div>
        </div>
      </a-spin>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import {listApi} from '/@/api/animal';
import {BASE_URL} from "/@/store/constants";

const router = useRouter();
const loading = ref(false);
const animalList = ref([]);

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

const goDetail = (pid) => {
  router.push({name: 'animalDetail', query: {pid}});
};

onMounted(() => {
  loading.value = true;
  listApi({}).then((res) => {
    loading.value = false;
    animalList.value = res.data || [];
  }).catch(() => { loading.value = false; });
});
</script>

<style scoped lang="less">
.animal-list-page { min-height: 100vh; }
.content { max-width: 1200px; margin: 0 auto; padding: 80px 24px 40px; }
.page-title { text-align: center; margin-bottom: 32px;
  h2 { font-size: 28px; color: #333; margin-bottom: 8px; }
  p { color: #999; font-size: 14px; }
}
.card-list { display: flex; flex-wrap: wrap; gap: 24px; }
.card-item { width: calc(25% - 18px); background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); cursor: pointer; transition: transform 0.3s;
  &:hover { transform: translateY(-4px); box-shadow: 0 4px 16px rgba(0,0,0,0.15); }
}
.card-img { height: 200px; overflow: hidden;
  img { width: 100%; height: 100%; object-fit: cover; }
  .no-img { height: 100%; display: flex; align-items: center; justify-content: center; background: #f5f5f5; color: #999; }
}
.card-info { padding: 16px;
  h3 { font-size: 18px; color: #333; margin-bottom: 8px; }
  p { font-size: 14px; color: #666; margin-bottom: 4px;
    .label { color: #999; }
  }
}
.no-data { text-align: center; padding: 80px 0; color: #999; font-size: 16px; width: 100%; }
</style>