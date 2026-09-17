<template>
  <div class="goods-buy-page">
    <Header />
    <div class="content">
      <h2>确认购买</h2>
      <div class="buy-card" v-if="goods">
        <div class="goods-info">
          <img v-if="goods.img" :src="getImgUrl(goods.img)" class="goods-img" />
          <div class="info">
            <h3>{{ goods.name }}</h3>
            <p class="price">¥{{ goods.price }}</p>
          </div>
        </div>
        <a-divider />
        <a-form :label-col="{ style: { width: '80px' } }" :model="form">
          <a-form-item label="购买数量">
            <a-input-number v-model:value="form.num" :min="1" :max="goods.num" style="width: 200px;" />
          </a-form-item>
          <a-form-item label="收货人">
            <a-input v-model:value="form.username" placeholder="请输入收货人姓名" />
          </a-form-item>
          <a-form-item label="联系电话">
            <a-input v-model:value="form.phone" placeholder="请输入联系电话" />
          </a-form-item>
          <a-form-item label="收货地址">
            <a-textarea v-model:value="form.address" placeholder="请输入收货地址" :rows="2" />
          </a-form-item>
          <a-form-item label="备注">
            <a-input v-model:value="form.remark" placeholder="备注信息（选填）" />
          </a-form-item>
          <a-form-item>
            <div class="total-row">
              <span>总计：<b style="color: #ff4d4f; font-size: 24px;">¥{{ goods.price * form.num }}</b></span>
            </div>
            <a-button type="primary" size="large" @click="handleSubmit" :loading="submitting">提交订单</a-button>
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
import {detailApi} from '/@/api/goods';
import {createApi} from '/@/api/order';
import {BASE_URL} from "/@/store/constants";
import {message} from 'ant-design-vue';

const route = useRoute();
const router = useRouter();
const goods = ref(null);
const submitting = ref(false);
const form = reactive({num: 1, username: '', phone: '', address: '', remark: ''});

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

const handleSubmit = () => {
  if (!form.username || !form.phone || !form.address) {
    message.warn('请填写收货信息');
    return;
  }
  submitting.value = true;
  createApi({
    goodsId: goods.value.id,
    userId: localStorage.getItem('user_id'),
    count: form.num,
    receiverName: form.username,
    receiverPhone: form.phone,
    receiverAddress: form.address,
    remark: form.remark,
  }).then(() => {
    submitting.value = false;
    message.success('下单成功');
    router.push({name: 'myOrder'});
  }).catch(() => {
    submitting.value = false;
    message.error('下单失败');
  });
};

onMounted(() => {
  const goodsId = route.query.goodsId;
  if (goodsId) {
    detailApi({id: goodsId}).then((res) => { goods.value = res.data; });
  }
});
</script>

<style scoped lang="less">
.goods-buy-page { min-height: 100vh; }
.content { max-width: 800px; margin: 0 auto; padding: 40px 24px;
  h2 { font-size: 24px; color: #333; margin-bottom: 24px; }
}
.buy-card { background: #fff; padding: 32px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
.goods-info { display: flex; gap: 16px; align-items: center;
  .goods-img { width: 120px; height: 120px; object-fit: cover; border-radius: 8px; }
  .info {
    h3 { font-size: 18px; color: #333; }
    .price { color: #ff4d4f; font-size: 24px; font-weight: bold; margin-top: 8px; }
  }
}
.total-row { margin-bottom: 16px; font-size: 16px; }
</style>