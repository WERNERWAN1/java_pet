<template>
  <div class="goods-list-page">
    <Header />
    <div class="content">
      <div class="page-title">
        <h2>宠物用品</h2>
        <p>为你的爱宠挑选好物</p>
      </div>
      <a-spin :spinning="loading" style="min-height: 200px;">
        <div class="card-list">
          <div v-for="item in goodsList" :key="item.id" class="card-item">
            <div class="card-img">
              <img v-if="item.img" :src="getImgUrl(item.img)" />
              <div v-else class="no-img">暂无图片</div>
            </div>
            <div class="card-info">
              <h3>{{ item.name }}</h3>
              <div class="price-row">
                <span class="price">¥{{ item.price }}</span>
                <span class="stock">库存: {{ item.num }}</span>
              </div>
              <a-button type="primary" block @click="handleBuy(item)" :disabled="item.num <= 0">
                {{ item.num > 0 ? '立即购买' : '已售罄' }}
              </a-button>
            </div>
          </div>
          <div v-if="goodsList.length === 0 && !loading" class="no-data">暂无商品</div>
        </div>
      </a-spin>
    </div>
    <Footer />
  </div>
</template>

<script setup>
import Header from '/@/views/index/components/header.vue';
import Footer from '/@/views/index/components/footer.vue';
import {listApi} from '/@/api/goods';
import {BASE_URL} from "/@/store/constants";
import {message} from 'ant-design-vue';

const router = useRouter();
const loading = ref(false);
const goodsList = ref([]);

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

const handleBuy = (item) => {
  const userId = localStorage.getItem('user_id');
  if (!userId) {
    message.warn('请先登录');
    router.push({name: 'login'});
    return;
  }
  router.push({name: 'goodsBuy', query: {goodsId: item.id}});
};

onMounted(() => {
  loading.value = true;
  listApi({}).then((res) => {
    loading.value = false;
    goodsList.value = res.data || [];
  }).catch(() => { loading.value = false; });
});
</script>

<style scoped lang="less">
.goods-list-page { min-height: 100vh; }
.content { max-width: 1200px; margin: 0 auto; padding: 80px 24px 40px; }
.page-title { text-align: center; margin-bottom: 32px;
  h2 { font-size: 28px; color: #333; margin-bottom: 8px; }
  p { color: #999; font-size: 14px; }
}
.card-list { display: flex; flex-wrap: wrap; gap: 24px; }
.card-item { width: calc(25% - 18px); background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.1); transition: transform 0.3s;
  &:hover { transform: translateY(-4px); }
}
.card-img { height: 200px; overflow: hidden;
  img { width: 100%; height: 100%; object-fit: cover; }
  .no-img { height: 100%; display: flex; align-items: center; justify-content: center; background: #f5f5f5; color: #999; }
}
.card-info { padding: 16px;
  h3 { font-size: 16px; color: #333; margin-bottom: 8px; }
  .price-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;
    .price { color: #ff4d4f; font-size: 20px; font-weight: bold; }
    .stock { color: #999; font-size: 12px; }
  }
}
.no-data { text-align: center; padding: 80px 0; color: #999; font-size: 16px; width: 100%; }
</style>