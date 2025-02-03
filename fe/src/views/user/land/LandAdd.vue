<!-- eslint-disable no-undef -->
<script setup>
import { onMounted, ref } from "vue";
import NoItem from '@/components/common/NoItem.vue';
import RouterBack from "@/components/common/RouterBack.vue";
import defaultThumbnail from "@/assets/images/default/default_thumbnail.png";
import { landInsert } from "@/services/user/user.land";

const infraOptions = ref([
	"엘리베이터", "와이파이", "휠체어 접근", "반려동물 허용", "버스 정류장",
	"지하철역", "주차", "에어컨", "난방", "주방", "화장실", "보안"
]);


// ✅ 상태 변수 정의
const insert = ref({
	price: "",
	zipcode: "",
	address: "",
	addrDetail: "",
	description: "",
	infra: [],
	title: "",
	area: "",
	ageGroup: "",
});

const uploadThumb = ref(null);
const uploadImg = ref(null)

const thumbnail = ref([])
const images = ref([]);

const previewSrc = ref(defaultThumbnail);

const setDefaultFile = async () => {
	const response = await fetch(defaultThumbnail);
	const blob = await response.blob();
	thumbnail.value = new File([blob], "default_thumbnail.png", { type: blob.type });
};

onMounted(() => {
	setDefaultFile()
})


const formattedArea = (e) => {
	let inputValue = e.target.value;

	let filteredValue = inputValue.replace(/\D/g, '');

	if (filteredValue > 200) {
		filteredValue = "200";
	}

	e.target.value = filteredValue;
};

const formattedPrice = (e) => {
	let inputValue = e.target.value;

	// 숫자만 남기기
	let filteredValue = inputValue.replace(/\D/g, '');

	// 최대값 제한 (1000만 원 이하)
	if (parseInt(filteredValue, 10) > 10000000) {
		filteredValue = "10000000";
	}

	// 세 자리마다 콤마 추가 (천 단위 구분)

	// 입력 필드 업데이트
	e.target.value = filteredValue;
};


const handleThumbnail = (event) => {
	const file = event.target.files[0];
	if (file) {
		const reader = new FileReader();
		reader.onload = (e) => {
			thumbnail.value = e.target.result;
		};
		reader.readAsDataURL(file);
	}

	if (file) {
		// 파일 검증
		if (!file.type.match("image/.*")) {
			alert('이미지 파일만 업로드가 가능합니다.');
			return;
		}
		if (file.size > 1024 ** 2 * 2) {
			alert('이미지의 크기를 2MB 이하로 업로드 해주시기 바랍니다.');
			return;
		}

		thumbnail.value = file;

		const reader = new FileReader();
		reader.onload = (e) => {
			previewSrc.value = e.target.result;
		};
		reader.readAsDataURL(file);
	}
};

const uploadThumbnail = () => {
	uploadThumb.value.click();
}

const uploadImage = () => {
	uploadImg.value.click();
}

const handleImages = (event) => {
	const files = event.target.files;
	if (files.length + images.value.length > 10) {
		alert("최대 10개까지만 업로드 가능합니다.");
		return;
	}

	let limit = 1024 ** 2 * 2;
	[...files].forEach(file => {
		if (!file.type.match("image/.*")) {
			alert('이미지 파일만 업로드가 가능합니다.');
			return;
		}

		if (file.size > limit) {
			alert('이미지의 크기를 2MB 이하로 업로드 해주시기 바랍니다.');
			return;
		}

		if (images.value.length >= 10) {
			alert('이미지는 최대 10개까지 등록할 수 있습니다.')
			return;
		}
		const reader = new FileReader();
		reader.onload = (e) => {
			images.value.push({ src: e.target.result, name: file.name });
		};
		reader.readAsDataURL(file);
	});
	event.target.value = "";
};

const removeImage = (index) => {
	images.value.splice(index, 1);
};

const addInfra = (infra) => {
	if (!insert.value.infra.includes(infra)) {
		insert.value.infra.push(infra);
	}
};

const removeInfra = (infra) => {
	insert.value.infra = insert.value.infra.filter((item) => item !== infra);
};

const daumPostcode = () => {
	new daum.Postcode({
		oncomplete: (data) => {
			insert.value.zipcode = data.zonecode;
			insert.value.address = data.roadAddress;
		},
	}).open();
};

const insertData = async () => {
	try {
		let formData = new FormData();

		if (thumbnail.value) {
			formData.append("thumbnail", thumbnail.value);
		}

		insert.value.infra = insert.value.infra.join(',');

		formData.append("rentalPlace", new Blob([JSON.stringify(insert.value)], { type: "application/json" }));

		images.value.forEach(file => formData.append("images", file));

		await landInsert(formData);

		alert("임대지 추가에 성공했습니다.");
		router.push("/user/land");
	} catch (err) {
		alert("임대지 추가에 실패했습니다.");
		console.error(err);
	}
};

</script>

<template>
	<main class="flex-grow flex flex-col items-center">
		<RouterBack link="/user/land" text="임대지 추가" />
		<section class="flex mt-4 w-full justify-center">
			<form class="max-w-4xl px-4 flex flex-col w-full bg-white" enctype="multipart/form-data">
				<div class="flex sm:space-x-2 space-x-0 sm:flex-row flex-col">
					<div class="space-y-4 flex flex-col flex-shrink-0 justify-center items-center p-8">
						<img id="thumbnail-image" :src="previewSrc"
							class="w-40 h-40 border border-gray-300 object-cover bg-white" alt="">
						<input type="file" ref="uploadThumb" accept="image/gif, image/png, image/jpeg" class="hidden"
							@change="handleThumbnail">
						<button id="thumbnail-custom-upload" @click="uploadThumbnail"
							class="p-2 bg-[#3FB8AF] text-white font-bold rounded-lg transition-colors hover:bg-[#2c817c]"
							type="button">이미지 변경하기</button>
						<span class="text-xs font-bold block">2MB 이하로 업로드 해주세요.</span>
					</div>
					<div class="flex flex-col flex-grow p-8 space-y-4">
						<div class="bg-white min-w-60 border px-4 border-gray-300 rounded-md p-2">
							<label class="font-bold">임대지 이름</label>
							<input v-model="insert.title" type="text"
								class="w-full h-9 p-1 flex-grow focus:outline-[#3FB8AF]" placeholder="제목을 입력하시기 바랍니다."
								required>
						</div>
						<div class="bg-white px-4 py-2 flex-1 border border-gray-300 rounded-md">
							<label class="font-bold">
								금액 <span class="font-bold text-xs">(숫자만 입력해주시기 바랍니다.)</span>
							</label>
							<div class="flex items-center space-x-2">
								<input v-model="insert.price" @input="formattedPrice" type="text"
									placeholder="금액을 입력해주시기 바랍니다." class="w-full h-9 p-1 focus:outline-[#3FB8AF]"
									required>
								<span class="div min-w-20 font-bold">원 / 일</span>
							</div>
						</div>
						<div>
							<div class="bg-white px-4 py-2 flex-1 border border-gray-300 rounded-md">
								<span class="font-bold">임대지 주소</span>
								<div class="flex flex-col space-y-2">
									<div class="flex justify-between items-center space-x-4">
										<input type="text" placeholder="우편번호" v-model="insert.zipcode"
											class="py-2 min-w-28 focus:outline-[#3FB8AF]" readonly required>
										<button type="button" @click="daumPostcode()"
											class="py-2 px-4 bg-[#3FB8AF] flex-shrink-0 text-white font-bold rounded-lg transition-colors hover:bg-[#2c817c]">
											주소 찾기
										</button>
									</div>
									<input type="text" placeholder="주소" v-model="insert.address"
										class="py-2 focus:outline-[#3FB8AF]" readonly required>
									<input type="text" placeholder="상세 주소" v-model="insert.addrDetail"
										class="py-2 focus:outline-[#3FB8AF]" required>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="flex flex-col">
					<div class="pb-2 border-b border-gray-300 flex justify-between">
						<span>이미지 목록 (최대 10개)</span>
						<span id="images-count">{{ images.length }}개</span>
					</div>
					<ul id="image-list-box" class="mt-4 mb-4">
						<li v-if="images.length === 0">
							<NoItem message="이미지를 추가해주세요." />
						</li>
						<li v-for="(image, index) in images" :key="index"
							class="flex items-center space-x-4 px-8 py-2 hover:bg-gray-200">
							<img :src="image.src" class="w-20 h-20 border border-gray-300 object-contain bg-gray-300">
							<span class="text-xs font-bold flex-1">{{ image.name }}</span>
							<button type="button" @click="removeImage(index)"><i
									class="fas fa-xmark text-red-500"></i></button>
						</li>
					</ul>
					<div class="flex justify-between border-t pt-2 border-gray-300">
						<span class="text-xs font-bold flex-1">이미지 하나당 2MB 이하로 업로드 해주세요.</span>
						<input type="file" ref="uploadImg" accept="image/gif, image/png, image/jpeg" class="hidden"
							@change="handleImages" multiple>
						<button type="button" @click="uploadImage()"
							class="py-2 px-4 bg-[#3FB8AF] flex-grow-0 text-white font-bold rounded-lg transition-colors hover:bg-[#2c817c]">
							이미지 추가
						</button>
					</div>
				</div>
				<div class="flex flex-col mt-4 border-gray-300">
					<div class="pb-2 border-b border-gray-300">
						<span>인프라 설정</span>
					</div>
					<div class="m-8 border border-gray-300 rounded-lg">
						<div class="p-2 mx-2 border-b border-gray-300">
							<span class="text-xs font-bold">선택된 인프라 목록</span>
							<ul id="my-infra-box"
								class="m-2 flex border-gray-300 overflow-x-auto space-x-2 whitespace-nowrap list_scrollbar py-2 cursor-default">
								<li v-if="insert.infra.length === 0">
									<NoItem message="인프라를 추가해주세요." />
								</li>
								<li v-for="infra in insert.infra" :key="infra"
									class="text-xs px-3 py-2 flex hover:border-red-500 transition-colors items-center border-2 border-[#3fb8af] rounded-full space-x-2">
									<span class="font-bold">{{ infra }}</span>
									<button type="button" @click="removeInfra(infra)"><i
											class="fas fa-xmark text-red-500 font-bold"></i></button>
								</li>
							</ul>
						</div>
						<div class="p-2 mx-2">
							<span class="text-xs font-bold">버튼을 눌러 인프라를 추가하시기 바랍니다.</span>
							<div
								class="overflow-x-auto space-x-2 whitespace-nowrap list_scrollbar py-2 m-2 cursor-default">
								<button v-for="infra in infraOptions" :key="infra" @click="addInfra(infra)"
									type="button"
									class="border-gray-500 border-2 bg-[#3fb8af] px-3 py-2 text-xs font-bold text-white rounded-full focus:outline-white hover:bg-[#2c817c] transition-colors">
									{{ infra }}
								</button>
							</div>
						</div>
					</div>
				</div>
				<div class="flex flex-col mt-4 border-gray-300">
					<div class="pb-2 border-b border-gray-300">
						<span>추가 설정</span>
					</div>
					<div class="p-8 flex flex-col space-y-4">
						<div class="flex justify-between space-x-4">
							<div class="bg-white px-4 py-2 flex-1 border border-gray-300 rounded-md">
								<label class="font-bold">주변 연령대</label>
								<select v-model="insert.ageGroup" class="w-full h-9 p-1 focus:outline-[#3FB8AF]">
									<option value="">전체</option>
									<option value="10대">10대</option>
									<option value="20대">20대</option>
									<option value="30대">30대</option>
									<option value="40대">40대</option>
									<option value="50대">50대</option>
									<option value="60대 이상">60대 이상</option>
								</select>
							</div>
							<div class="bg-white px-4 py-2 flex-1 border border-gray-300 rounded-md">
								<label class="font-bold">면적<span class="font-bold text-xs">(평)</span></label>
								<div class="flex items-center space-x-2">
									<input v-model="insert.area" @input="formattedArea" type="text" placeholder="면적"
										class="w-full h-9 p-1 focus:outline-[#3FB8AF]" required>
									<span class="div font-bold">평</span>
								</div>
							</div>
						</div>
						<div class="bg-white px-4 py-2 flex-1 border border-gray-300 rounded-md">
							<label class="font-bold">상세 설명</label>
							<textarea v-model="insert.description" class="w-full h-40 focus:outline-[#3FB8AF]"
								placeholder="설명을 입력해주시기 바랍니다." required></textarea>
						</div>
					</div>
					<div class="flex justify-between border-t pt-2 border-gray-300">
						<span class="text-xs font-bold flex-1">하단의 [추가하기] 버튼을 눌러 임대지를 추가해주세요.</span>
						<button type="button" @click="insertData"
							class="py-2 px-4 bg-[#3FB8AF] flex-grow-0 text-white font-bold rounded-lg transition-colors hover:bg-[#2c817c]">추가하기</button>
					</div>
				</div>
			</form>
		</section>
	</main>
</template>