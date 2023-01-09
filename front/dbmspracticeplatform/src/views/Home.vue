<template>
  <div class="home">I'm HOme</div>
</template>

<script>
import { mapState } from "vuex";

export default {
  name: "Home",
  data() {
    return {};
  },
  computed: {
    ...mapState(["username"]), // 用户名
  },
};
</script>

<style lang="css">
.home {
  height: 100%;
  width: 1000px;
  background-color: yellow;
}
</style>
