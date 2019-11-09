<template>
    <div v-if="isLogin">
        <el-tabs type="border-card">
            <el-tab-pane label="Add New Record Group">
                <el-form ref="recordGroupForm">
                    <el-form-item>
                        <el-input placeholder="Record Group Name" v-model="recordGroupName"></el-input>
                        <el-button v-on:click="addRecordGroup">Add New Record Group</el-button>
                    </el-form-item>
                </el-form>
            </el-tab-pane>
            <el-tab-pane label="Select From Existing Record Group">
                <el-form>
                    <el-form-item>
                        <el-select v-model="recordGroups" placeholder="Record Groups">
                            <el-option v-for="{recordGroup} in recordGroups"
                                       v-bind:key="recordGroup.id"
                                       v-bind:value="recordGroup.id">
                                {{ recordGroup.recordGroupName }}
                            </el-option>
                        </el-select>
                        <el-button v-on:click="readyForDisplayRecordUploaded">Select</el-button>
                    </el-form-item>
                </el-form>
            </el-tab-pane>
        </el-tabs>
        <div v-if="isReadyForDisplayRecordUploaded">
            <div id="author-record">
                <label>Author Record:</label>
                <span v-if="isAuthorRecordUploaded">Author Record Already Uploaded</span>
                <span v-else>Author Record Not Uploaded Yet</span>
            </div>
            <div id="review-record">
                <label>Review Record:</label>
                <span v-if="isReviewRecordUploaded">Review Record Already Uploaded</span>
                <span v-else>Review Record Not Uploaded Yet</span>
            </div>
            <div id="submission-record">
                <label>Submission Record:</label>
                <span v-if="isSubmissionRecordUploaded">Submission Record Already Uploaded</span>
                <span v-else>Submission Record Not Uploaded Yet</span>
            </div>
            <el-button>Upload</el-button>
            <import-data/>
        </div>
    </div>
</template>

<script>
    import ImportData from "@/components/ImportData.vue";
    import {ID_NEW_RECORDGROUP} from "../common/const";
    export default {
        name: "SelectRecordGroup",
        data() {
            return {
                recordGroupName: '',
                isReadyForDisplayRecordUploaded: false
            }
        },
        computed: {
            isLogin() {
                return this.$store.state.userInfo.isLogin;
            },
            isAppLoading: function () {
                return this.$store.state.isPageLoading || this.$store.state.dbMetaData.entitiesStatus.isLoading;
            },
            dbSchemas: function () {
                return this.$store.state.dbMetaData.entities;
            },
            recordGroups() {
                return this.$store.state.recordGroup.recordGroupList;
            }
        },
        methods: {
            readyForDisplayRecordUploaded: function () {
                this.isReadyForDisplayRecordUploaded = true;
                this.$store.dispatch('getRecordList');
            },
            addRecordGroup() {
                this.$refs['recordGroupForm'].validate((valid) => {
                    if (!valid) {
                        return;
                    }
                    this.$refs['recordGroupForm'].clearValidate();
                    // add
                    this.$store.dispatch('saveRecordGroup')
                        .then(() => {
                            if (this.isError) {
                                return
                            }
                            // redirect to the newly added recordGroup
                            this.$router.push({
                                name: 'importData',
                                params: {
                                    id: this.$store.state.recordGroup.recordGroupForm.id
                                }
                            });
                        });
                });
            },
            deleteRecordGroup() {
                this.$store.dispatch('deleteRecordGroup', this.id)
                    .then(() => {
                        if (this.isError) {
                            return
                        }
                        this.$router.replace({
                            name: 'importData',
                            params: {
                                id: ID_NEW_RECORDGROUP
                            }
                        });
                        this.isEditing = false;
                    })
            },
            updateRecordGroupForm() {
                if (this.$refs['recordGroupForm']) {
                    this.$refs['recordGroupForm'].clearValidate();
                }
                this.$store.commit('resetRecordGroupForm');
            },
        },
        components: {
            ImportData
        },
        mounted() {
            this.$store.dispatch('getRecordGroupList');
        }
    }
</script>

<style scoped>
     .el-button {
         margin-top: 20px;
     }
    .el-select {
        width: 100%;
    }
</style>