<script setup>
import RouterBack from '@/components/common/RouterBack.vue';
import { onMounted, ref, computed } from 'vue';
import { getUserInfo, updateUserInfo, deleteUserInfo } from '@/services/user/user.api';
import { useRouter } from 'vue-router';
import LoadingSpinner from '@/components/common/LoadingSpinner.vue';

const router = useRouter();

const info = ref(null);

const upload = ref(null);
const selectedFile = ref(null);
const previewSrc = ref('');

const brand = ref('');
const password = ref('');
const passwordOk = ref('');

// 유효성 검사용 상태
const brandChecked = ref(false);
const brandExists = ref(false);

const passwordTouched = ref(false);
const passwordOkTouched = ref(false);

const isPasswordVisible = ref(false);
const isPasswordOkVisible = ref(false);

const isLoading = ref(true);

onMounted(async () => {
	await fetchUserInfo();
});

// 사용자 정보 가져오기
const fetchUserInfo = async () => {
	try {
		const result = await getUserInfo();
		info.value = result;
		brand.value = result.brand || '';
		previewSrc.value = result.profileImage;
	} catch (err) {
		console.error('API 요청 오류:', err);
	} finally {
		isLoading.value = false;
	}
};

// 프로필 이미지 변경
const handleFileChange = (event) => {
	const files = event.target.files;
	if (files && files.length > 0) {
		const file = files[0];

		// 파일 검증
		if (!file.type.match("image/.*")) {
			alert('이미지 파일만 업로드가 가능합니다.');
			return;
		}
		if (file.size > 1024 ** 2 * 2) {
			alert('이미지의 크기를 2MB 이하로 업로드 해주시기 바랍니다.');
			return;
		}

		selectedFile.value = file;

		previewSrc.value = URL.createObjectURL(file);
	}
};

// 비밀번호 보이기 토글
const togglePasswordVisibility = () => {
	isPasswordVisible.value = !isPasswordVisible.value;
};
const togglePasswordOkVisibility = () => {
	isPasswordOkVisible.value = !isPasswordOkVisible.value;
};

const passwordError = computed(() => {
	const regex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
	return !regex.test(password.value);
});

const passwordOkError = computed(() => {
	return password.value !== passwordOk.value;
});

const checkBrand = () => {
	// 로직 추가 필요
	if (brand.value.trim() === '중복된닉네임') {
		brandExists.value = true;
	} else {
		brandExists.value = false;
	}
	brandChecked.value = true;
};

// 사용자 정보 업데이트
const updateUser = async () => {
	try {
		const formData = new FormData();

		const data = {
			"password": password.value,
			"brand": brand.value
		}

		formData.append('data', new Blob([JSON.stringify(data)], { type: 'application/json' }));

		if (selectedFile.value) {
			formData.append('profileImage', selectedFile.value);
		}

		await updateUserInfo(formData);
		alert('정보가 수정되었습니다.');
		await fetchUserInfo();
	} catch (err) {
		console.error('정보 수정 오류:', err);
		alert('정보 수정에 실패했습니다.');
	}
};

// 회원 탈퇴
const deleteUser = async () => {
	if (confirm('정말 탈퇴하시겠습니까?')) {
		try {
			await deleteUserInfo();
			alert('회원 탈퇴가 완료되었습니다.');
			localStorage.removeItem('token');
			router.push('/');
		} catch (err) {
			console.error('회원 탈퇴 오류:', err);
			alert('회원 탈퇴에 실패했습니다.');
		}
	}
};

const isFormValid = computed(() => {
	const isBrandValid = brandChecked.value && !brandExists.value;
	const isPasswordValid = password.value.length >= 8 && !passwordError.value && !passwordOkError.value;

	return isBrandValid && isPasswordValid;
});

</script>

<template>
	<LoadingSpinner v-if="isLoading" />
	<main v-else class="flex-grow flex flex-col items-center">
		<RouterBack link="/user" text="회원 정보" />
		<section class="flex mt-4 w-full bg-white justify-center">
			<form class="min-w-5xl space-y-12 p-8 flex-col" enctype="multipart/form-data" onsubmit="return false;">
				<div class="flex flex-col justify-between space-y-8">
					<div class="flex flex-col">
						<label class="ms-2 text-gray-700 font-bold">프로필</label>
						<div class="flex flex-col justify-center items-center p-2 mt-2 space-y-4 rounded-md">
							<img :src="previewSrc"
								class="rounded-full w-40 h-40 border-2 border-gray-300 object-contain bg-white" alt="">
							<input type="file" ref="upload" accept="image/gif, image/png, image/jpeg" class="hidden"
								@change="handleFileChange">
							<button @click="upload.click()"
								class="h-12 border p-2 bg-[#3FB8AF] hover:bg-[#2c817c] text-white font-bold transition-colors rounded-md"
								type="button">이미지 변경</button>
							<span class="text-xs font-bold">2MB 이하로 업로드 해주세요.</span>
						</div>
					</div>
					<div class="flex flex-col space-y-4">
						<div class="flex flex-col">
							<label class="ms-2 text-gray-700 font-bold">이메일</label>
							<div class="flex w-96 border-2 border-gray-300 p-2 mt-2 space-x-2 rounded-md">
								<div class="h-12 p-2 font-bold items-center flex border-2 border-white">
									{{ info?.email }}
								</div>
							</div>
						</div>
						<div class="flex flex-col">
							<label for="password" class="ms-2 text-gray-700 font-bold">비밀번호<span
									class="ms-2 text-left text-red-500 text-sm">*</span></label>
							<div class="flex w-96 border-2 border-gray-300 p-2 mt-2 space-x-2 rounded-md">
								<input :type="isPasswordVisible ? 'text' : 'password'" v-model="password" required
									id="password" @blur="passwordTouched = true" autocomplete="new-password"
									class="h-12 p-2 flex-1 focus-visible:outline-[#3FB8AF]"
									placeholder="8자리 이상 영문, 숫자, 특수문자 포함" />
								<div class="h-12 p-2 flex items-center justify-center transition-colors rounded-md w-10 text-gray-500 cursor-pointer"
									@click="togglePasswordVisibility">
									<i v-if="isPasswordVisible" class="fas fa-eye"></i>
									<i v-else class="fas fa-eye-slash"></i>
								</div>
							</div>
							<span v-if="passwordError && passwordTouched" class="ms-2 text-left text-red-500 text-sm">
								비밀번호가 양식에 맞지 않습니다.
							</span>
							<div class="flex w-96 border-2 border-gray-300 p-2 mt-2 space-x-2 rounded-md">
								<input :type="isPasswordOkVisible ? 'text' : 'password'"
									@blur="passwordOkTouched = true" v-model="passwordOk" autocomplete="new-password"
									class="h-12 p-2 flex-1 focus-visible:outline-[#3FB8AF]" required
									placeholder="비밀번호 확인" />
								<div class="h-12 p-2 flex items-center justify-center transition-colors rounded-md w-10 text-gray-500 cursor-pointer"
									@click="togglePasswordOkVisibility">
									<i v-if="isPasswordOkVisible" class="fas fa-eye"></i>
									<i v-else class="fas fa-eye-slash"></i>
								</div>
							</div>
							<span v-if="passwordOkError && passwordOkTouched"
								class="ms-2 text-left text-red-500 text-sm">
								비밀번호가 일치하지 않습니다.
							</span>
						</div>
					</div>
					<div class="flex flex-col space-y-4">
						<div class="flex flex-col">
							<label class="ms-2 text-gray-700 font-bold">이름</label>
							<div class="flex w-96 border-2 border-gray-300 p-2 mt-2 space-x-2 rounded-md">
								<div class="h-12 p-2 font-bold items-center flex border-2 border-white">
									{{ info?.name }}
								</div>
							</div>
						</div>
						<div class="flex flex-col">
							<label class="ms-2 text-gray-700 font-bold">
								{{ info?.role === 'LANDLORD' ? "상호명" : "닉네임" }}<span
									class="ms-2 text-left text-red-500 text-sm">*
								</span>
							</label>
							<div class="flex w-96 border-2 border-gray-300 p-2 mt-2 space-x-2 rounded-md">
								<input type="text" @blur="brandTouched = true" @input="resetBrandCheck" v-model="brand"
									class="h-12 p-2 flex-1 focus-visible:outline-none focus-visible:border-[#3FB8AF] border-2 border-white transition-colors"
									:placeholder="info?.role === 'LANDLORD' ? '상호명 입력' : '닉네임 입력'" />
								<button @click="checkBrand" :disabled="!brand" type="button"
									class="h-12 border p-2 bg-[#3FB8AF] hover:bg-[#2c817c] disabled:bg-gray-300 text-white font-bold transition-colors rounded-md">확인</button>
							</div>
							<span v-if="brandChecked && brandExists" class="ms-2 text-left text-red-500 text-sm">
								이미 사용 중인 {{ info?.role === 'LANDLORD' ? "상호명" : "닉네임" }}입니다.
							</span>
							<span v-if="brandChecked && !brandExists" class="ms-2 text-left text-blue-500 text-sm">
								사용이 가능한 {{ info?.role === 'LANDLORD' ? "상호명" : "닉네임" }}입니다.
							</span>
						</div>
						<div class="flex flex-col">
							<label class="ms-2 text-gray-700 font-bold">전화번호</label>
							<div class="flex w-96 border-2 border-gray-300 p-2 mt-2 space-x-2 rounded-md">
								<div class="h-12 p-2 font-bold items-center flex border-2 border-white">
									{{ info?.tel }}
								</div>
							</div>
						</div>
						<div v-if="info?.role === 'LANDLORD'" class="flex flex-col">
							<label class="ms-2 text-gray-700 font-bold">사업자등록번호</label>
							<div class="flex w-96 border-2 border-gray-300 p-2 mt-2 space-x-2 rounded-md">
								<div class="h-12 p-2 font-bold items-center flex border-2 border-white">
									{{ info?.businessId }}
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="flex justify-between w-full">
					<button type="button" @click="deleteUser"
						class="text-white bg-red-500 hover:bg-red-700 p-2 rounded-md transition-colors font-bold">
						회원 탈퇴
					</button>
					<button type="button" :disabled="!isFormValid"
						:class="['text-white font-bold p-2 rounded-md transition-colors', isFormValid ? 'bg-[#3FB8AF] hover:bg-[#2c817c]' : 'bg-gray-300 cursor-not-allowed']"
						@click="updateUser">정보 수정</button>
				</div>
			</form>
		</section>
	</main>
</template>