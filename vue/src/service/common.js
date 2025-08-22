  import { useStore } from "@/stores/store";
  import axios from "axios";
  import { storeToRefs } from "pinia";
  import { ref } from "vue";

  export const commonApi = async(url, type, data) => {
    const res = ref([]);
    const store = useStore();
    const { member } = storeToRefs(store);

    try {
      const instance = axios.create({
        baseURL : "http://localhost:8081",
        // baseURL : "http://58.236.44.179:8081",
      });

      const header = {
        headers: {
          "Content-Type": "multipart/form-data",
        },
        // withCredentials : true,
      };

      // if (url.includes("logout")) {
      //   instance.defaults.withCredentials = false;

      // } else {
      //   instance.defaults.withCredentials = true;
      // }

      instance.defaults.withCredentials = true;

      if (url.includes("getFile")) {
        // 서버에서 이미지 데이터를 blob 형태로 받는다.
        instance.defaults.responseType = "blob";
      }

      instance.defaults.paramsSerializer = (param) => {
        const params = new URLSearchParams();
        for (const key in param) {
            params.append(key, param[key]);
        }

        return params.toString();
      }
      
      switch (type.toUpperCase()) {
        case "GET":
          res.value = await instance.get(url, {params: data === undefined ? {} : data});
          // res.value = await instance.get(url);
          break;

        case "POST":
          if (url.includes("login") || url.includes("Upload")) {
            res.value = await instance.post(url, data, header);

          } else {
            res.value = await instance.post(url, data);
          }
          break;

        case "PATCH":
          res.value = await instance.patch(url, data);
          break;

        case "DELETE":
          res.value = await instance.delete(url, {params: data === undefined ? {} : data});
          // res.value = await instance.delete(url);
          break;
          
        default:
          break;
      }
      
      return res.value;

    } catch (e) {
      console.error(e.response);
      return e.response;
    }
  }