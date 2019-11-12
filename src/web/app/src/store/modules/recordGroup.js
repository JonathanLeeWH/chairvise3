import axios from 'axios'
import {deepCopy} from "@/common/utility"

export default {
    state: {
        recordGroupList: [],
        recordGroupListStatus: {
            isLoading: true,
            isApiError: false,
            apiErrorMsg: '',
        },
        recordGroupForm: {
            id: '',
            recordGroupName: '',
            dataset:'',
            authorRecordUploadStatus:false,
            reviewRecordUploadStatus:false,
            submissionRecordUploadStatus:false
        },
        newRecordGroupForm: {
            recordGroupName: '',
            dataset:'',
            authorRecordUploadStatus:false,
            reviewRecordUploadStatus:false,
            submissionRecordUploadStatus:false
        },
        recordGroupFormStatus: {
            isLoading: false,
            isApiError: false,
            apiErrorMsg: '',
        },
        isRecordGroupEditable: false,
        isDeleteRecordGroupSuccess: false,
        isAddRecordGroupSuccess: false,
        isUpdateRecordGroupSuccess: false
    },
    mutations: {
        setRecordGroupListLoading(state, payload) {
            if (payload) {
                state.recordGroupListStatus.isApiError = false;
            }
            state.recordGroupListStatus.isLoading = payload;
        },

        setRecordGroupListApiError(state, payload) {
            state.recordGroupListStatus.isApiError = true;
            state.recordGroupListStatus.apiErrorMsg = payload;
        },

        setRecordGroupList(state, payload) {
            state.recordGroupList = payload;
        },

        addToRecordGroupList(state, payload) {
            state.recordGroupList.push(payload);
        },

        deleteFromRecordGroupList(state, payload) {
            const index = state.recordGroupList.findIndex(recordGroup => recordGroup.id === payload);
            state.recordGroupList.splice(index, 1)
        },

        updateRecordGroupListWith(state, payload) {
            state.recordGroupList = state.recordGroupList.map(recordGroup => {
                if (recordGroup.id === payload.id) {
                    return payload
                }
                return recordGroup
            });
        },

        setRecordGroupFormLoading(state, isLoading) {
            state.recordGroupFormStatus.isLoading = isLoading;
        },

        setRecordGroupFormApiError(state, msg) {
            state.recordGroupFormStatus.isApiError = true;
            state.recordGroupFormStatus.apiErrorMsg = msg;
        },

        setRecordGroupForm(state, payload) {
            state.recordGroupForm = payload;
        },

        resetRecordGroupForm(state) {
            state.recordGroupForm.id = '';
            state.recordGroupForm.name = '';
            state.recordGroupForm.authorRecordUploaded = false;
            state.recordGroupForm.reviewRecordUploaded = false;
            state.recordGroupForm.submissionRecordUploaded = false;
            state.recordGroupForm.dataset = '';
            state.recordGroupFormStatus.isLoading = false;
            state.recordGroupFormStatus.isApiError = false;
            state.recordGroupFormStatus.apiErrorMsg = '';
        },

        setRecordGroupFormField(state, {field, value}) {
            state.recordGroupForm[field] = value
        },

        setNewRecordGroupFormField(state, {field, value}) {
            state.newRecordGroupForm[field] = value
        },

        setIsRecordGroupEditable(state, isRecordGroupEditable) {
            state.isRecordGroupEditable = isRecordGroupEditable;
        },

        setAddRecordGroupSuccess(state, success) {
            state.isAddRecordGroupSuccess = success;
        },

        setUpdateRecordGroupSuccess(state, success) {
            state.isUpdateRecordGroupSuccess = success;
        },

        setDeleteRecordGroupSuccess(state, success) {
            state.isDeleteRecordGroupSuccess = success;
        },

    },
    actions: {
        async getRecordGroupList({commit}) {
            commit('setRecordGroupListLoading', true);
            axios.get('/api/record/record_groups')
                .then(response => {
                    commit('setRecordGroupList', response.data)
                })
                .catch(e => {
                    commit('setRecordGroupListApiError', e.toString());
                })
                .finally(() => {
                    commit('setRecordGroupListLoading', false);
                })
        },

        async getRecordGroup({commit}, recordGroupId) {
            commit('setRecordGroupFormLoading', true);
            await axios.get(`/api/record/record_groups/${recordGroupId}`)
                .then(response => {
                    commit('setRecordGroupForm', response.data)
                })
                .catch(e => {
                    commit('setRecordGroupFormApiError', e.toString());
                })
                .finally(() => {
                    commit('setRecordGroupFormLoading', false);
                });
        },

        async saveRecordGroup({commit, state}) {
            commit('setRecordGroupFormLoading', true);
            await axios.post('/api/record/record_groups', state.newRecordGroupForm)
                .then(response => {
                    commit('addToRecordGroupList', deepCopy(response.data));
                    commit('setRecordGroupForm', deepCopy(response.data))
                })
                .catch(e => {
                    commit('setRecordGroupFormApiError', e.toString());
                })
                .finally(() => {
                    commit('setRecordGroupFormLoading', false);
                })
        },

        async updateRecordGroup({commit, state}) {
            commit('setRecordGroupFormLoading', true);
            await axios.put('/api/record/record_groups/' + state.recordGroupForm.id, state.recordGroupForm)
                .then(response => {
                    commit('updateRecordGroupListWith', response.data)
                })
                .catch(e => {
                    commit('setRecordGroupFormApiError', e.toString());
                })
                .finally(() => {
                    commit('setRecordGroupFormLoading', false);
                })
        },

        async deleteRecordGroup({commit}, payload) {
            commit('setRecordGroupFormLoading', true);
            await axios.delete('/api/record/record_groups/' + payload)
                .then(() => {
                    commit('deleteFromRecordGroupList', payload);
                    commit('resetRecordGroupForm');
                    commit('setDeleteRecordGroupSuccess', true);
                })
                .catch(e => {
                    commit('setRecordGroupFormApiError', e.toString());
                })
                .finally(() => {
                    commit('setRecordGroupFormLoading', false);
                })
        },
    }
};