import { jsonRequest, multipartRequest } from '@/services/api';

export const getUserInfo = async () => {

	const response = await jsonRequest(`/user`, 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};

export const updateUserInfo = async (data) => {

	await multipartRequest(`/user`, 'PUT', data);

	return { success: true, message: "사용자 정보가 업데이트되었습니다." };
};

export const deleteUserInfo = async () => {

	await jsonRequest(`/user`, 'DELETE');

	return { success: true, message: "사용자 계정이 삭제되었습니다." };
};

export const userHome = async () => {

	const response = await jsonRequest(`/user/home`, 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}