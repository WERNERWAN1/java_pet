<template>
  <div class="detail">
    <Header/>
    <div class="detail-content" v-if="detailData.id">
      <!-- 商品信息 -->
      <div class="thing-infos-view">
        <div class="thing-infos">
          <div class="thing-img-box">
            <img :src="detailData.cover" alt="">
          </div>
          <div class="thing-info-box">
            <div class="thing-state">
              <span class="state">{{ detailData.status == 1 ? '在售' : '下架' }}</span>
            </div>
            <h2 class="thing-name">{{ detailData.title }}</h2>
            <div class="authors">
              <span class="title">发布者：</span>
              <span class="name">{{ detailData.nickname }}</span>
            </div>
            <div class="translators">
              <span class="title">地址：</span>
              <span>{{ detailData.address }}</span>
            </div>
            <div class="translators">
              <span class="title">库存：</span>
              <span>{{ detailData.stock || 0 }} 件</span>
            </div>
            <div class="buy-way">
              <span class="price">¥{{ detailData.price }}</span>
              <button class="buy-btn" :disabled="detailData.stock <= 0" @click="handleOrder(detailData)">{{ detailData.stock > 0 ? '立即购买' : '已售罄' }}</button>
            </div>
          </div>
          <div class="thing-counts">
            <div class="count-box">
              <div class="count-item">
                <div class="count-img"><img :src="RecommendIcon" alt=""></div>
                <div class="count-text-box">
                  <span class="count-title">推荐数</span>
                </div>
                <div class="count-num-box">{{ detailData.recommendCount || 0 }}</div>
              </div>
            </div>
            <div class="count-box">
              <div class="count-item">
                <div class="count-img"><img :src="WantIcon" alt=""></div>
                <div class="count-text-box">
                  <span class="count-title">心愿数</span>
                </div>
                <div class="count-num-box">{{ detailData.wishCount || 0 }}</div>
              </div>
            </div>
            <div class="count-item" @click="addToWish">
              <div class="count-img"><img :src="AddIcon" alt=""></div>
              <div class="count-text-box">
                <span class="count-title">加心愿单</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 详情内容 -->
      <div class="thing-content-view">
        <div class="flex-view">
          <div class="main-content">
            <div class="main-tab">
              <a-button
                  v-for="(item, index) in tabData"
                  :key="index"
                  class="tab-btn"
                  :type="selectTabIndex === index ? 'primary' : 'default'"
                  @click="selectTab(index)"
              >{{ item }}</a-button>
            </div>
            <!-- 简介 -->
            <div v-if="selectTabIndex == 0">
              <div class="intro-box" ref="introBoxRef" :class="{ 'intro-collapsed': introOverflow && !introExpanded }">
                <div class="text" v-if="detailData.description">{{ detailData.description }}</div>
                <div class="text intro-empty" v-else>暂无简介</div>
              </div>
              <div class="intro-toggle" v-if="introOverflow" @click="introExpanded = !introExpanded">
                {{ introExpanded ? '收起' : '展开全部' }}
              </div>
            </div>
            <!-- 评论 -->
            <div v-if="selectTabIndex == 1">
              <div class="thing-comment">
                <div class="publish">
                  <a-textarea
                      v-model:value="commentText"
                      class="content-input"
                      :maxlength="500"
                      :auto-size="{ minRows: 3, maxRows: 6 }"
                      show-count
                      :placeholder="isLogin ? '说点什么...' : '登录后即可发表评论'"
                  />
                  <a-button type="primary" class="send-btn" :loading="sending" @click="sendComment">发布</a-button>
                </div>
                <div class="tab-view">
                  <span class="count-text">共{{ commentData.length }}条评论</span>
                  <div class="tab-box">
                    <span :class="{ 'tab-select': sortIndex == 0 }" @click="sortCommentList('recent')">最新</span>
                    <span class="line"></span>
                    <span :class="{ 'tab-select': sortIndex == 1 }" @click="sortCommentList('hot')">最热</span>
                  </div>
                </div>
              </div>
              <a-spin :spinning="commentLoading">
                <div class="comments-list">
                  <div class="comment-item" v-for="item in commentData" :key="item.id">
                    <div class="flex-item">
                      <img class="avator" :src="AvatarIcon" alt="">
                      <div class="person">
                        <span class="name">{{ item.nickname || item.username || '匿名用户' }}</span>
                        <div class="time">{{ item.commentTime }}</div>
                      </div>
                      <div class="float-right">
                        <span :class="{ liked: item.liked }" @click="like(item)">赞<span class="num">{{ item.likeCount || 0 }}</span></span>
                      </div>
                    </div>
                    <div class="comment-content">{{ item.content }}</div>
                  </div>
                  <a-empty v-if="!commentLoading && commentData.length === 0" description="还没有评论，快来抢沙发吧" />
                </div>
              </a-spin>
            </div>
          </div>
          <!-- 推荐 -->
          <div class="recommend">
            <h3 class="title">相关推荐</h3>
            <div class="things">
              <div class="thing-item" v-for="item in recommendData" :key="item.id" @click="handleDetail(item)">
                <div class="img-view">
                  <img :src="item.cover" alt="">
                </div>
                <div class="info-view">
                  <div class="thing-name">{{ item.title }}</div>
                  <div class="price">¥{{ item.price }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import {message} from "ant-design-vue";
import Header from '/@/views/index/components/header.vue'
import Footer from '/@/views/index/components/footer.vue'
import AddIcon from '/@/assets/images/add.svg';
import WantIcon from '/@/assets/images/want-read-hover.svg';
import RecommendIcon from '/@/assets/images/recommend-hover.svg';
import ShareIcon from '/@/assets/images/share-icon.svg';
import WeiboShareIcon from '/@/assets/images/wb-share.svg';
import AvatarIcon from '/@/assets/images/avatar.jpg';
import {
  detailApi,
  listApi as listThingList,
} from '/@/api/petProduct'
import {listProductCommentsApi, createApi as createCommentApi, likeApi} from '/@/api/comment'
import {wishApi} from '/@/api/petProductWish'
import {collectApi} from '/@/api/petProductCollect'
import {BASE_URL} from "/@/store/constants";
import {useRoute, useRouter} from "vue-router/dist/vue-router";
import {useUserStore} from "/@/store";
import {getFormatTime} from "/@/utils";

const router = useRouter()
const route = useRoute()
const userStore = useUserStore();


let thingId = ref('')
let detailData = ref({})
let tabUnderLeft = ref(6)
let tabData = computed(() => ['简介', `评论${commentData.value.length ? ` (${commentData.value.length})` : ''}`])
let selectTabIndex = ref(0)

let commentData = ref([])
let recommendData = ref([])
let sortIndex = ref(0)
let order = ref('recent') // 默认排序最新

// 评论输入与加载态
let commentText = ref('')
let sending = ref(false)
let commentLoading = ref(false)
// 简介过长时默认收起，点击展开
let introExpanded = ref(false)
let introOverflow = ref(false)
let introBoxRef = ref()
const isLogin = computed(() => !!(userStore.user_id || localStorage.getItem('user_id')))

onMounted(()=>{
  thingId.value = route.query.id.trim()
  getThingDetail()
  getRecommendThing()
  getCommentList()
})

const selectTab =(index)=> {
  selectTabIndex.value = index
  tabUnderLeft.value = 6 + 54 * index
}

const getThingDetail =()=> {
  detailApi({id: thingId.value}).then(res => {
    detailData.value = res.data
    detailData.value.cover = BASE_URL + '/api/staticfiles/image/' + detailData.value.cover
    // 内容渲染完成后测量实际高度，超过一屏再显示“展开全部”
    checkIntroOverflow()
  }).catch(err => {
    message.error('获取详情失败')
  })
}

const checkIntroOverflow = () => {
  nextTick(() => {
    const el = introBoxRef.value
    if (el) {
      introOverflow.value = el.scrollHeight > 280
    }
  })
}
const addToWish =()=> {
  let userId = userStore.user_id
  if (userId) {
    wishApi({productId: thingId.value, userId: userId}).then(res => {
      message.success(res.msg)
      getThingDetail()
    }).catch(err => {
      console.log('操作失败')
    })
  } else {
    message.warn('请先登录')
  }
}
const collect =()=> {
  let userId = userStore.user_id
  if (userId) {
    collectApi({productId: thingId.value, userId: userId}).then(res => {
      message.success(res.msg)
      getThingDetail()
    }).catch(err => {
      console.log('收藏失败')
    })
  } else {
    message.warn('请先登录')
  }
}
const share =()=> {
  let content = '分享一个非常好玩的网站 ' + window.location.href
  let shareHref = 'http://service.weibo.com/share/share.php?title=' + content
  window.open(shareHref)
}
const handleOrder =(detailData)=> {
  console.log(detailData)
  const userId = userStore.user_id
  if (!userId) {
    message.warn('请先登录')
    return
  }
  if (detailData.stock <= 0) {
    message.warn('该商品已售罄')
    return
  }
  router.push({name: 'confirm',
    query:
        {
          id: detailData.id,
          title: detailData.title,
          cover: detailData.cover,
          price: detailData.price,
          stock: detailData.stock
        }})
}
const getRecommendThing =()=> {
  listThingList({sort: 'recommend'}).then(res => {
    res.data.forEach((item, index) => {
      if (item.cover) {
        item.cover = BASE_URL + '/api/staticfiles/image/' + item.cover
      }
    })
    console.log(res)
    recommendData.value = res.data.slice(0, 6)
  }).catch(err => {
    console.log(err)
  })
}
const handleDetail =(item)=> {
  // 跳转新页面
  let text = router.resolve({name: 'detail', query: {id: item.id}})
  window.open(text.href, '_blank')
}
const sendComment =()=> {
  const text = commentText.value.trim()
  if (!text) {
    message.warn('请输入评论内容')
    return
  }
  const userId = userStore.user_id || localStorage.getItem('user_id')
  if (!userId) {
    // 不跳转登录，避免输入内容丢失
    message.warn('请先登录后再发表评论')
    return
  }
  sending.value = true
  createCommentApi({content: text, productId: thingId.value, userId: userId}).then(res => {
    message.success('评论成功')
    // 发布后清空输入并切到最新排序，保证新评论立即可见
    commentText.value = ''
    sortIndex.value = 0
    order.value = 'recent'
    getCommentList()
    sending.value = false
  }).catch(err => {
    message.error(err.msg || '评论失败')
    sending.value = false
  })
}
const like =(item)=> {
  if (item.liked) {
    return
  }
  const userId = userStore.user_id || localStorage.getItem('user_id')
  if (!userId) {
    message.warn('请先登录！')
    return
  }
  likeApi({id: item.id}).then(res => {
    // 本地就地更新，避免整列表重排导致阅读位置丢失
    item.liked = true
    item.likeCount = Number(item.likeCount || 0) + 1
  }).catch(err => {
    message.error(err.msg || '点赞失败')
  })
}
const getCommentList =()=> {
  commentLoading.value = true
  listProductCommentsApi({productId: thingId.value, order: order.value}).then(res => {
    const list = res.data || []
    list.forEach(item => {
      item.commentTime = getFormatTime(item.commentTime, true)
    })
    commentData.value = list
    commentLoading.value = false
  }).catch(err => {
    commentLoading.value = false
    message.error('获取评论失败')
  })
}
const sortCommentList =(sortType)=> {
  if (sortType === 'recent') {
    sortIndex.value = 0
  } else {
    sortIndex.value = 1
  }
  order.value = sortType
  getCommentList()
}

</script>
<style scoped lang="less">

.hide {
  display: none;
}

.detail-content {
  display: flex;
  flex-direction: column;
  width: 1100px;
  margin: 4px auto;
}

.flex-view {
  display: -webkit-box;
  display: -ms-flexbox;
  display: flex;
}

.hidden-lg {
  display: none !important;
}

.thing-infos-view {
  display: flex;
  margin: 89px 0 40px;
  overflow: hidden;

  .thing-infos {
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    display: flex;
  }

  .mobile-share-box {
    height: 38px;
    background: transparent;
    padding: 0 16px;
    margin: 12px 0;
    font-size: 0;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;

    .state {
      width: 64px;
      height: 24px;
      line-height: 24px;
      background: rgba(70, 132, 226, .1);
      border-radius: 2px;
      font-weight: 500;
      font-size: 12px;
      color: #4684e2;
      text-align: center;
    }

    .share-img {
      background: #fff;
      width: 38px;
      height: 38px;
      border-radius: 50%;
      text-align: center;

      img {
        position: relative;
        top: 4px;
        width: 24px;
      }
    }
  }

  .thing-img-box {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 235px;
    flex: 0 0 235px;
    margin: 0 40px 0 0;

    img {
      width: 255px;
      height: 200px;
      display: block;
      background-size: cover;
      object-fit: cover;
    }
  }

  .thing-info-box {
    text-align: left;
    padding: 0;
    margin: 0;
  }

  .thing-state {
    height: 26px;
    line-height: 26px;

    .state {
      font-weight: 500;
      color: #4684e2;
      background: rgba(70, 132, 226, .1);
      border-radius: 2px;
      padding: 5px 8px;
      margin-right: 16px;
    }

    span {
      font-size: 14px;
      color: #152844;
    }
  }

  .thing-name {
    line-height: 32px;
    margin: 16px 0;
    color: #0F1111!important;
    font-size: 18px!important;
    font-weight: 400!important;
    font-style: normal!important;
    text-transform: none!important;
    text-decoration: none!important;
  }

  .translators, .authors {
    line-height: 18px;
    font-size: 14px;
    margin: 8px 0;
    -webkit-box-align: start;
    -ms-flex-align: start;
    align-items: flex-start;
    -webkit-box-pack: start;
    -ms-flex-pack: start;
    justify-content: flex-start;

    .name {
      color: #315c9e;
      white-space: normal;
    }
  }

  .tags {
    position: absolute;
    bottom: 20px;
    margin-top: 16px;

    .category-box {
      color: #152844;
      font-size: 14px;

      .title {
        color: #787878;
      }
    }
  }

  .thing-counts {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 235px;
    flex: 0 0 235px;
    margin-left: 20px;
  }

  .pointer {
    cursor: pointer;
  }

  .count-item {
    height: 64px;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    cursor: pointer;
  }

  .count-img {
    -webkit-box-flex: 0;
    -ms-flex: 0 0 32px;
    flex: 0 0 32px;
    margin-right: 24px;
    font-size: 0;

    img {
      width: 100%;
      display: block;
    }
  }

  .count-box {
    position: relative;
    border-bottom: 1px solid #cedce4;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    height: 100%;
  }

  .count-text-box {
    font-size: 0;

    .count-title {
      color: #152844;
      font-weight: 600;
      font-size: 16px;
      line-height: 18px;
      display: block;
      height: 18px;
    }
  }

  .count-num-box {
    font-weight: 600;
    font-size: 20px;
    line-height: 24px;
    color: #152844;
  }
}

.buy-btn {
  cursor: pointer;
  display: block;
  background: #4684e2;
  border-radius: 4px;
  text-align: center;
  color: #fff;
  font-size: 14px;
  height: 36px;
  line-height: 36px;
  width: 110px;
  outline: none;
  border: none;
}

.buy-btn img {
  width: 12px;
  margin-right: 2px;
  vertical-align: middle;
}

.buy-btn span {
  vertical-align: middle;
}

.buy-way {
  display: flex;
  align-items: center;
  margin-top: 16px;

  .price {
    color: #ff7b31;
    font-size: 24px;
    font-weight: 600;
    margin-right: 20px;
  }
}

.thing-content-view {
  margin-top: 40px;
  padding-bottom: 50px;
}

.main-content {
  -webkit-box-flex: 1;
  -ms-flex: 1;
  flex: 1;

  .text {
    color: #484848;
    font-size: 16px;
    line-height: 26px;
    padding-left: 12px;
    margin: 11px 0;
    white-space: pre-wrap;
  }

  .intro-box {
    position: relative;

    &.intro-collapsed {
      max-height: 260px;
      overflow: hidden;

      &::after {
        content: '';
        position: absolute;
        left: 0;
        right: 0;
        bottom: 0;
        height: 64px;
        background: linear-gradient(rgba(255, 255, 255, 0), #fff);
      }
    }
  }

  .intro-empty {
    color: #999;
  }

  .intro-toggle {
    display: inline-block;
    margin: 4px 0 8px 12px;
    color: #4684e2;
    font-size: 14px;
    cursor: pointer;
    user-select: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.main-tab {
  display: flex;
  gap: 12px;
  padding: 0 12px 12px;
  border-bottom: 1px solid #cedce4;

  .tab-btn {
    min-width: 96px;
    height: 36px;
    font-size: 14px;
  }
}

.order-view {
  position: relative;
  color: #6c6c6c;
  font-size: 14px;
  line-height: 40px;

  .title {
    margin-right: 8px;
  }

  .tab {
    margin-right: 20px;
    cursor: pointer;
    color: #5f77a6;
    font-size: 16px;
    cursor: pointer;
  }

  .tab-select {
    color: #152844;
    font-weight: 600;
  }

  .tab-underline {
    position: absolute;
    bottom: 0;
    left: 84px;
    width: 16px;
    height: 4px;
    background: #4684e2;
    -webkit-transition: left .3s;
    transition: left .3s;
  }
}

.recommend {
  -webkit-box-flex: 0;
  -ms-flex: 0 0 235px;
  flex: 0 0 235px;
  margin-left: 20px;

  .title {
    font-weight: 600;
    font-size: 18px;
    line-height: 26px;
    color: #152844;
    margin-bottom: 12px;
  }

  .things {
    border-top: 1px solid #cedce4;

    .thing-item {
      min-width: 255px;
      max-width: 255px;
      position: relative;
      flex: 1;
      margin-right: 20px;
      height: fit-content;
      overflow: hidden;
      margin-top: 26px;
      margin-bottom: 36px;
      padding-bottom: 24px;
      border-bottom: 1px #e1e1e1 solid;
      cursor: pointer;

      .img-view {
        //background: #f3f3f3;
        //text-align: center;
        height: 200px;
        width: 255px;
        //border: 1px #f3f3f3 solid;

        img {
          height: 200px;
          width: 255px;
          overflow: hidden;
          margin: 0 auto;
          background-size: cover;
          object-fit: cover;
        }
      }

      .info-view {
        //background: #f6f9fb;
        overflow: hidden;
        padding: 0 16px;
        .thing-name {
          line-height: 32px;
          margin-top: 12px;
          color: #0F1111!important;
          font-size: 15px!important;
          font-weight: 400!important;
          font-style: normal!important;
          text-transform: none!important;
          text-decoration: none!important;
        }

        .price {
          color: #ff7b31;
          font-size: 20px;
          line-height: 20px;
          margin-top: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .translators {
          color: #6f6f6f;
          font-size: 12px;
          line-height: 14px;
          margin-top: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

  }
}

.flex-view {
  display: flex;
}

.thing-comment {
  .title {
    font-weight: 600;
    font-size: 14px;
    line-height: 22px;
    height: 22px;
    color: #152844;
    margin: 24px 0 12px;
  }

  .publish {
    -webkit-box-align: start;
    -ms-flex-align: start;
    align-items: flex-start;

    .mine-img {
      -webkit-box-flex: 0;
      -ms-flex: 0 0 40px;
      flex: 0 0 40px;
      margin-right: 12px;
      border-radius: 50%;
      width: 40px;
      height: 40px;
    }

    .content-input {
      -webkit-box-flex: 1;
      -ms-flex: 1;
      flex: 1;
      background: #f6f9fb;
      border-radius: 4px;
      color: #484848;
      padding: 8px 12px;
      font-size: 14px;
      line-height: 22px;
      outline: none;
      border: 0px;
      resize: none;
    }

    .send-btn {
      margin-left: 12px;
      -webkit-box-flex: 0;
      -ms-flex: 0 0 80px;
      flex: 0 0 80px;
      height: 40px;
    }
  }

  .tab-view {
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    font-size: 14px;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    margin: 24px 0;

    .count-text {
      color: #484848;
      float: left;
    }

    .tab-box {
      color: #5f77a6;
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;

      .tab-select {
        color: #152844;
      }

      span {
        cursor: pointer;
      }
    }

    .line {
      width: 1px;
      height: 12px;
      margin: 0 12px;
      background: #cedce4;
    }
  }
}


.comments-list {
  .comment-item {
    .flex-item {
      -webkit-box-align: center;
      -ms-flex-align: center;
      align-items: center;
      padding-top: 16px;

      .avator {
        -webkit-box-flex: 0;
        -ms-flex: 0 0 40px;
        flex: 0 0 40px;
        width: 40px;
        height: 40px;
        margin-right: 12px;
        border-radius: 50%;
        cursor: pointer;
      }

      .person {
        -webkit-box-flex: 1;
        -ms-flex: 1;
        flex: 1;
      }

      .name {
        color: #152844;
        font-weight: 600;
        font-size: 14px;
        line-height: 22px;
        height: 22px;
        cursor: pointer;
      }

      .time {
        color: #5f77a6;
        font-size: 12px;
        line-height: 16px;
        height: 16px;
        margin-top: 2px;
      }

      .float-right {
        color: #4684e2;
        font-size: 14px;
        float: right;

        span {
          margin-left: 19px;
          cursor: pointer;
        }

        .liked {
          color: #ff4d4f;
          cursor: default;
        }

        .num {
          color: #152844;
          margin-left: 6px;
          cursor: auto;
        }
      }
    }
  }
}

.comment-content {
  margin-top: 8px;
  color: #484848;
  font-size: 14px;
  line-height: 22px;
  padding-bottom: 16px;
  border-bottom: 1px dashed #cedce4;
  margin-left: 52px;
  overflow: hidden;
  word-break: break-word;
  white-space: pre-wrap;
}

.infinite-loading-container {
  clear: both;
  text-align: center;
}

.a-price-symbol {
  top: -0.5em;
  font-size: 12px;
}
.a-price {
  color: #0F1111;
  font-size:21px;
}
</style>
