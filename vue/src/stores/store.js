import { ref, computed, reactive } from 'vue'
import { defineStore } from 'pinia'
import { commonApi } from '@/service/common';

export const useStore = defineStore('store', () => {
  const member = ref({
    id : null,
    sessionId : null,
    role : null,
  });

  const pager = ref({
    pageNum : 1,
    amount : 10,
  });

  const cart = ref({
    count : 0,
    checkArr : [],
  });

  const getCartCount = async (id) => {
    try {
      let param = {}
      param.id = id;
      const res = await commonApi("/api/cart/getCount", "get", param);
      cart.value.count = res.data;
       
    } catch (e) {
      console.log(e);
    }
  };

  // 오늘 시간대별 데이터 계산
  // const hours = computed(() => { // '?.' : 옵셔널 체이닝
  //   return days.value
  //   // 날씨 객체 배열에서 오늘 날짜와 일치하는 객체 1개를 찾음
  //   ?.find((v) => v.datetime === dayjs().format("YYYY-MM-DD"))
  //   // 현재 시각 이후 시간만 시간별 데이터에 포함
  //   ?.hours.filter((v) => v.datetime >= dayjs().format("HH:mm:ss"));
  // });

  const PostCodeApi = (item) => {
    new window.daum.Postcode({
      oncomplete: (data) => {
        if (item.address3 !== "") {
          item.address3 = "";
        }

        if (data.userSelectedType === "R") {
          // 사용자가 도로명 주소를 선택했을 경우
          item.address1 = data.roadAddress;
        } else {
          // 사용자가 지번 주소를 선택했을 경우(J)
          item.address1 = data.jibunAddress;
        }

        // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
        if (data.userSelectedType === "R") {
          // 법정동명이 있을 경우 추가한다. (법정리는 제외)
          // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
          if (data.bname !== "" && /[동|로|가]$/g.test(data.bname)) {
            item.address3 += data.bname;
          }

          // 건물명이 있고, 공동주택일 경우 추가한다.
          if (data.buildingName !== "" && data.apartment === "Y") {
            item.address3 += item.address3 !== "" ? `, ${data.buildingName}` : data.buildingName;
          }

          // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
          if (item.address3 !== "") {
            item.address3 = `(${item.address3})`;
          }

        } else {
          item.address3 = "";
        }
        // 우편번호를 입력한다.
        item.postCode = data.zonecode;

        // console.log(item);
      },
    }).open();
  };

  return {  
    member,
    pager,
    cart,
    getCartCount,
    PostCodeApi,
  }
}, {persist:true});
