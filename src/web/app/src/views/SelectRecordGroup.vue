<template>
    <div>
        <div v-if="isLogin">
            <div class="row">
                <el-alert v-if="isError" :title="apiErrorMsg" type="error" show-icon class="errorMsg" />
            </div>
            <p></p>
            <el-tabs type="border-card">
                <el-tab-pane label="Add New Record Group">
                    <el-form :model="newRecordGroupForm" ref="newRecordGroupForm" :rules="rules">
                        <el-form-item label="Record Group Name" prop="name">
                            <el-input v-model="newRecordGroupFormName"></el-input>
                            <el-button type="success" v-on:click="addRecordGroup">Add New Record Group</el-button>
                        </el-form-item>
                    </el-form>
                    <el-form :model="newRecordGroupForm" ref="recordGroupForm" class="recordUploaded"
                             v-if="isReadyForAddRecord">
                        <div class="upload-status">
                            <label>Record Group Name: </label>
                            <el-input :prop="'name'" v-model="recordGroupFormName">{{ recordGroupName }}</el-input>
                            <el-button type="warning" v-on:click="updateRecordGroup" icon="el-icon-edit">Edit
                            </el-button>
                            <el-button type="danger" v-on:click="deleteRecordGroup" icon="el-icon-delete">Delete
                            </el-button>
                        </div>
                        <div class="upload-status author-record">
                            <label>Author Record: </label>
                            <span class="uploaded" v-if="isAuthorRecordUploaded">Already Uploaded</span>
                            <span class="not-uploaded" v-else>Not Uploaded Yet</span>
                        </div>
                        <div class="upload-status review-record">
                            <label>Review Record: </label>
                            <span class="uploaded" v-if="isReviewRecordUploaded">Already Uploaded</span>
                            <span class="not-uploaded" v-else>Not Uploaded Yet</span>
                        </div>
                        <div class="upload-status submission-record">
                            <label>Submission Record: </label>
                            <span class="uploaded" v-if="isSubmissionRecordUploaded">Already Uploaded</span>
                            <span class="not-uploaded" v-else>Not Uploaded Yet</span>
                        </div>
                        <import-data />
                    </el-form>
                </el-tab-pane>
                <el-tab-pane label="Select Existing Record Group">
                    <el-form>
                        <el-form-item>
                            <el-select v-model="recordGroupId" placeholder="Record Groups">
                                <el-option v-for="recordGroup in recordGroups"
                                           :key="recordGroup.recordGroupName"
                                           :label="recordGroup.recordGroupName"
                                           :value="recordGroup.id">
                                </el-option>
                            </el-select>
                            <el-button type="success" v-on:click="readyForDisplayRecordUploaded">Select</el-button>
                        </el-form-item>
                    </el-form>
                    <el-form :model="newRecordGroupForm" ref="recordGroupForm" class="recordUploaded"
                             v-if="isReadyForDisplayRecordUploaded">
                        <div class="upload-status">
                            <label>Record Group Name: </label>
                            <el-input :prop="'name'" v-model="recordGroupFormName">{{ recordGroupName }}</el-input>
                            <div class="row">
                                <el-button type="warning" v-on:click="updateRecordGroup" icon="el-icon-edit">Edit
                                </el-button>
                                <el-button type="danger" v-on:click="deleteRecordGroup" icon="el-icon-delete">Delete
                                </el-button>
                            </div>
                        </div>
                        <div class="upload-status author-record">
                            <label>Author Record: </label>
                            <span class="uploaded" v-if="isAuthorRecordUploaded">Already Uploaded</span>
                            <span class="not-uploaded" v-else>Not Uploaded Yet</span>
                        </div>
                        <div class="upload-status review-record">
                            <label>Review Record: </label>
                            <span class="uploaded" v-if="isReviewRecordUploaded">Already Uploaded</span>
                            <span class="not-uploaded" v-else>Not Uploaded Yet</span>
                        </div>
                        <div class="upload-status submission-record">
                            <label>Submission Record: </label>
                            <span class="uploaded" v-if="isSubmissionRecordUploaded">Already Uploaded</span>
                            <span class="not-uploaded" v-else>Not Uploaded Yet</span>
                        </div>
                        <import-data />
                    </el-form>
                </el-tab-pane>
            </el-tabs>
        </div>
    </div>
</template>

<script>
  import ImportData from "@/components/ImportData.vue";

  export default {
    name: "SelectRecordGroup",
    data() {
      return {
        recordGroupName: '',
        isReadyForDisplayRecordUploaded: false,
        isReadyForAddRecord: false,
        selectedRecordGroupId: '',
        rules: {
          name: [
            {required: true, message: 'Please enter record group name', trigger: 'blur'},
            {min: 3, message: 'The length should be more than 3 character', trigger: 'blur'}
          ],
        },
      }
    },
    computed: {
      isError() {
        return this.$store.state.recordGroup.recordGroupFormStatus.isApiError
      },
      isLoading() {
        return this.$store.state.recordGroup.recordGroupFormStatus.isLoading
      },
      apiErrorMsg() {
        return this.$store.state.recordGroup.recordGroupFormStatus.apiErrorMsg
      },
      isLogin() {
        return this.$store.state.userInfo.isLogin;
      },
      isAppLoading: function () {
        return this.$store.state.isPageLoading || this.$store.state.dbMetaData.entitiesStatus.isLoading;
      },
      recordGroups() {
        return this.$store.state.recordGroup.recordGroupList;
      },
      recordGroupForm() {
        return {
          name: this.recordGroupFormName,
          authorRecordUploadStatus: this.$store.state.recordGroup.recordGroupForm.authorRecordUploadStatus,
          reviewRecordUploadStatus: this.$store.state.recordGroup.recordGroupForm.reviewRecordUploadStatus,
          submissionRecordUploadStatus:
          this.$store.state.recordGroup.recordGroupForm.submissionRecordUploadStatus
        }
      },
      recordGroupId: {
        get: function () {
          return this.selectedRecordGroupId;
        },
        set: function (newValue) {
          this.selectedRecordGroupId = newValue;
          this.isReadyForDisplayRecordUploaded = false;
        }
      },
      recordGroupFormName: {
        get() {
          return this.$store.state.recordGroup.recordGroupForm.recordGroupName
        },
        set(value) {
          this.$store.commit('setRecordGroupFormField', {
            field: 'recordGroupName',
            value
          })
        },
      },
      isAuthorRecordUploaded() {
        return this.$store.state.recordGroup.recordGroupForm.authorRecordUploadStatus
      },
      isReviewRecordUploaded() {
        return this.$store.state.recordGroup.recordGroupForm.reviewRecordUploadStatus
      },
      isSubmissionRecordUploaded() {
        return this.$store.state.recordGroup.recordGroupForm.submissionRecordUploadStatus
      },
      newRecordGroupForm() {
        return {
          name: this.newRecordGroupFormName,
          authorRecordUploadStatus: false,
          reviewRecordUploadStatus: false,
          submissionRecordUploadStatus: false
        }
      },
      newRecordGroupFormName: {
        get() {
          return this.$store.state.recordGroup.newRecordGroupForm.recordGroupName
        },
        set(value) {
          this.$store.commit('setNewRecordGroupFormField', {
            field: 'recordGroupName',
            value
          })
        },
      },
    },
    methods: {
      closeSuccess: function () {
        this.$store.commit('setAddRecordGroupSuccess', false);
        this.$store.commit('setUpdateRecordGroupSuccess', false);
        this.$store.commit('setDeleteRecordGroupSuccess', false);
        this.$store.commit('clearRecordGroup');
      },
      readyForDisplayRecordUploaded: function () {
        if (this.selectedRecordGroupId === '') {
          this.$message({
            type: 'error',
            message: 'Please select a record group.'
          });
          return;
        }

        this.isReadyForDisplayRecordUploaded = true;
        this.isReadyForAddRecord = false;
        this.$store.dispatch('getRecordGroup', this.selectedRecordGroupId);
        this.$store.commit("setRecordGroup", this.selectedRecordGroupId);
      },
      addRecordGroup() {
        this.$refs['newRecordGroupForm'].validate((valid) => {
          if (!valid) {
            return;
          }
          this.$refs['newRecordGroupForm'].clearValidate();
          // add
          this.$store.dispatch('saveRecordGroup')
              .then(() => {
                if (this.isError) {
                  return;
                }

                this.isReadyForAddRecord = true;
                this.isReadyForDisplayRecordUploaded = false;
                this.$store.dispatch('getRecordGroupList');
                this.$store.commit("setRecordGroup", this.$store.state.recordGroup.recordGroupForm.id);
                this.$store.commit('setAddRecordGroupSuccess', true);
                this.$confirm('You have successfully added a new record group!',
                    'Added Record Group', {
                      confirmButtonText: 'OK',
                      roundButton: true,
                      type: 'success',
                      showCancelButton: false
                    }).then(() => {
                  this.closeSuccess();
                })
              });
        });
      },
      deleteRecordGroup() {
        this.$confirm('This will delete the record group. Are you sure?', 'Deleting Record Group', {
          confirmButtonText: 'Yes',
          cancelButtonText: 'Cancel',
          roundButton: true,
          type: 'warning'
        }).then(() => {
          this.$store.dispatch('deleteRecordGroup', this.selectedRecordGroupId)
              .then(() => {
                if (this.isError) {
                  return;
                }

                this.isReadyForDisplayRecordUploaded = false;
                this.$store.commit('setDeleteRecordGroupSuccess', true);
                this.$message({
                  type: 'success',
                  message: 'Successfully deleted the record group!'
                });

                this.selectedRecordGroupId = '';
              })
        });
      },
      updateRecordGroup() {
        this.$refs['recordGroupForm'].validate((valid) => {
          if (!valid) {
            return;
          }
          this.$refs['recordGroupForm'].clearValidate();
          // update
          this.$store.dispatch('updateRecordGroup')
              .then(() => {
                if (this.isError) {
                  return;
                }

                this.$store.dispatch('getRecordGroupList');
                this.$store.commit('setUpdateRecordGroupSuccess', true);
                this.$confirm('You have successfully updated the record group!',
                    'Updated Record Group', {
                      confirmButtonText: 'OK',
                      roundButton: true,
                      type: 'success'
                    }).then(() => {
                  this.closeSuccess();
                });
              });
        });
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

    .uploaded {
        color: #28a745;
    }

    .not-uploaded {
        color: #dc3545;
        font-weight: bold;
    }

    .upload-status {
        padding: 10px 0;
    }
</style>
