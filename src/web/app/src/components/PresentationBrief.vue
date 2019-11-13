<template>
    <el-alert v-if="isNewPresentation && !isLogin" title="Please login to create new presentation" type="error"
              show-icon class="errorMsg" />
    <el-form v-else label-position="right" ref="presentationForm" label-width="120px" :rules="rules"
             :model="presentationForm" v-loading="isLoading">
        <el-alert v-if="isError" :title="apiErrorMsg" type="error" show-icon class="errorMsg" />
        <el-form-item label="Name" :prop=" isInEditMode ? 'name' : ''">
            <div v-if="!isInEditMode">{{ presentationForm.name }}</div>
            <el-input v-model="presentationFormName" v-if="isInEditMode" />
        </el-form-item>
        <el-form-item label="Access Control" v-if="!isNewPresentation">
            <el-tag>Created by {{ presentationForm.creatorIdentifier }}</el-tag>
            <el-button type="success" size="small" class="share_button_left_margin" icon="el-icon-view"
                       @click="openAccessControlPanel()" v-if="isLogin && isPresentationEditable">SHARE
            </el-button>
        </el-form-item>
        <el-dialog title="Share with other users:" :visible.sync="isAccessControlDialogVisible" width="70%"
                   :close-on-click-modal="false">
            <access-control-panel :presentationId="id"></access-control-panel>
        </el-dialog>
        <el-form-item label="Description">
            <div v-if="!isInEditMode" id="presentation-description">{{ presentationForm.description }}</div>
            <el-input v-model="presentationFormDescription" v-if="isInEditMode" />
        </el-form-item>
        <el-form-item label="Record Group" :prop="isInEditMode ? 'recordGroup' : ''">
            <div v-if="!isInEditMode" id="presentation-record-group">{{ presentationForm.recordGroupName }}</div>
            <el-select v-if="isInEditMode" v-model="presentationFormRecordGroupId"
                       placeholder="Record Groups">
                <el-option v-for="recordGroup in recordGroups"
                           :key="recordGroup.recordGroupName"
                           :label="recordGroup.recordGroupName"
                           :value="recordGroup.id">
                </el-option>
            </el-select>
        </el-form-item>
        <el-form-item>
            <el-dropdown @command="handleDownloadCommand" v-if="!isInEditMode && !isNewPresentation">
                <el-button type="primary" style="margin-right: 10px">
                    Export<i class="el-icon-arrow-down el-icon--right"></i>
                </el-button>
                <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="pdf">PDF Document (.pdf)</el-dropdown-item>
                    <el-dropdown-item command="email-self">Email as attachment</el-dropdown-item>
                </el-dropdown-menu>
            </el-dropdown>
            <el-button type="warning" @click="changeEditMode(true)" v-if="!isInEditMode && isPresentationEditable"
                       icon="el-icon-edit">Edit
            </el-button>
            <el-button type="success" @click="addPresentation()" v-if="isInEditMode" icon="el-icon-check">Save
            </el-button>
            <el-button type="info" @click="changeEditMode(false)" v-if="isInEditMode && !isNewPresentation"
                       icon="el-icon-close">Cancel
            </el-button>
            <el-button type="danger" v-if="!isNewPresentation && isLogin && isPresentationEditable"
                       icon="el-icon-delete" @click="deletePresentation(presentationForm.name)">Delete
            </el-button>
        </el-form-item>
    </el-form>
</template>

<script>
  import AccessControlPanel from '@/components/AccessControlPanel'
  import {download, send} from "@/store/helpers/pdfDownloader"
  import {AccessLevel, ID_NEW_PRESENTATION, SPECIAL_IDENTIFIER_PUBLIC} from "@/common/const";
  import {deepCopy} from "@/common/utility";

  export default {
    name: 'PresentationBrief',
    props: {
      id: String
    },
    mounted() {
      this.updatePresentationForm()
    },
    watch: {
      'id'() {
        this.updatePresentationForm()
      },
    },
    computed: {
      isLogin() {
        return this.$store.state.userInfo.isLogin
      },
      isPresentationEditable() {
        return this.$store.state.presentation.isPresentationEditable;
      },

      presentationForm() {
        this.$store.dispatch("getRecordGroupList");
        return {
          name: this.presentationFormName,
          creatorIdentifier: this.presentationFormCreatorIdentifier,
          description: this.presentationFormDescription,
          recordGroup: this.presentationFormRecordGroupId,
          recordGroupName: this.presentationFormRecordGroupName,
        }
      },
      presentationFormName: {
        get() {
          return this.$store.state.presentation.presentationForm.name
        },
        set(value) {
          this.$store.commit('setPresentationFormField', {
            field: 'name',
            value
          })
        },
      },
      presentationFormCreatorIdentifier() {
        return this.$store.state.presentation.presentationForm.creatorIdentifier
      },
      presentationFormDescription: {
        get() {
          return this.$store.state.presentation.presentationForm.description
        },
        set(value) {
          this.$store.commit('setPresentationFormField', {
            field: 'description',
            value
          })
        },
      },
      presentationFormRecordGroupId: {
        get: function () {
          return this.$store.state.presentation.presentationForm.recordGroupId;
        },
        set: function (value) {
          this.$store.commit('setPresentationFormField', {
            field: 'recordGroupId', value
          })
        }
      },
      presentationFormRecordGroupName: {
        get: function () {
          return this.$store.state.presentation.presentationForm.recordGroupName;
        },
        set: function (value) {
          this.$store.commit('setPresentationFormField', {
            field: 'recordGroupName', value
          })
        }
      },
      recordGroups() {
        return this.$store.state.recordGroup.recordGroupList;
      },
      isNewPresentation() {
        return this.id === ID_NEW_PRESENTATION
      },
      isInEditMode() {
        return this.isEditing || this.isNewPresentation
      },
      isLoading() {
        return this.$store.state.presentation.presentationFormStatus.isLoading
      },
      isError() {
        return this.$store.state.presentation.presentationFormStatus.isApiError
      },
      apiErrorMsg() {
        return this.$store.state.presentation.presentationFormStatus.apiErrorMsg
      }
    },
    data() {
      return {
        isEditing: false,
        isAccessControlDialogVisible: false,
        rules: {
          name: [
            {required: true, message: 'Please enter presentation name', trigger: 'blur'},
            {min: 3, message: 'The length should be more than 3 character', trigger: 'blur'}
          ],
          recordGroup: [
            {required: true, message: 'Please select a record group', trigger: 'blur'},
          ]
        },
        presentationRecordGroupId: ''
      }
    },
    methods: {
      changeEditMode(isEditing) {
        if (isEditing === false) {
          this.updatePresentationForm();
        }
        this.isEditing = isEditing;
      },

      openAccessControlPanel() {
        this.isAccessControlDialogVisible = true;
      },

      addPresentation() {
        this.$refs['presentationForm'].validate((valid) => {
          if (!valid) {
            return
          }
          this.$refs['presentationForm'].clearValidate();
          if (this.isNewPresentation) {
            // add
            this.$store.dispatch('savePresentation')
                .then(() => {
                  if (this.isError) {
                    return
                  }
                  // redirect to the newly added presentation
                  this.$router.push({
                    name: 'analyze',
                    params: {
                      id: this.$store.state.presentation.presentationForm.id
                    }
                  });
                });
          } else {
            // edit
            this.$store.dispatch('updatePresentation')
                .then(() => {
                  if (this.isError) {
                    return
                  }
                  this.isEditing = false
                })
          }
        });
      },
      deletePresentation(name) {
        this.$confirm('This will permanently delete the presentation "' + name + '". Are you sure?',
            'Deleting Presentation', {
              confirmButtonText: 'Yes',
              cancelButtonText: 'Cancel',
              roundButton: true,
              type: 'warning'
            }).then(() => {
          this.$store.dispatch('deletePresentation', this.id)
              .then(() => {
                if (this.isError) {
                  return
                }
                this.$router.replace({
                  name: 'analyze',
                  params: {
                    id: ID_NEW_PRESENTATION
                  }
                });
                this.isEditing = false;
              });
          this.$message({
            type: 'success',
            message: 'Successfully deleted the presentation!'
          });
        }).catch(() => {
          // Don't display any message
        });
      },
      updatePresentationForm() {
        if (this.$refs['presentationForm']) {
          this.$refs['presentationForm'].clearValidate();
        }

        this.$store.commit('resetPresentationForm');
        if (this.id !== ID_NEW_PRESENTATION) {
          this.$store.dispatch('getPresentation', this.id)
              .then(() => {
                // check writable or not
                this.$store.dispatch('fetchAccessControlList', this.id)
                    .then(() => {
                      let currentUser = this.$store.state.userInfo.userEmail;
                      let accessControlList = this.$store.state.accessControl.accessControlList;
                      let isPresentationEditable =
                          currentUser === this.presentationFormCreatorIdentifier
                          || accessControlList.some(
                          acl => acl.userIdentifier === currentUser && acl.accessLevel === AccessLevel.CAN_WRITE)
                          || accessControlList.some(
                          acl => acl.userIdentifier === SPECIAL_IDENTIFIER_PUBLIC && acl.accessLevel
                              === AccessLevel.CAN_WRITE);
                      this.$store.commit('setIsPresentationEditable', isPresentationEditable)
                    })
              })
        }
      },
      downloadPDF() {
        let vm = this;
        let wasPresentationEditable = deepCopy(vm.isPresentationEditable);
        vm.$store.commit('setIsPresentationEditable', false);
        vm.$store.commit('setPageLoadingStatus', true);

        this.$nextTick(() => {
          download(vm.presentationFormName).then(() => {
            vm.$store.commit('setIsPresentationEditable', wasPresentationEditable);
            vm.$store.commit('setPageLoadingStatus', false);
          });
        });
      },
      sendPDF() {
        let vm = this;
        let currentUrl = window.location.href;
        let wasPresentationEditable = deepCopy(vm.isPresentationEditable);
        vm.$store.commit('setIsPresentationEditable', false);
        vm.$store.commit('setPageLoadingStatus', true);

        this.$nextTick(() => {
          send(vm.presentationFormName)
              .then((blob) => {
                this.$store.dispatch('sendPresentation', {
                  jsonMessage: {
                    mailTo: [this.$store.state.userInfo.userEmail],
                    mailSubject: "Backup " + vm.presentationFormName + " Presentation",
                    mailContent: "A pdf copy of the " + vm.presentationFormName + " presentation is attached. " +
                        "You can view the presentation at " + currentUrl,
                    attachmentName: vm.presentationFormName + ".pdf"
                  },
                  pdfBlob: blob
                })
              })
              .then(() => {
                vm.$store.commit('setIsPresentationEditable', wasPresentationEditable);
                vm.$store.commit('setPageLoadingStatus', false);
                this.$message({
                  type: 'success',
                  message: 'Presentation has been successfully emailed to "' + this.$store.state.userInfo.userEmail
                      + '"!'
                });
              });
        });
      },

      handleDownloadCommand(command) {
        if (command === "pdf") {
          this.downloadPDF();
        } else if (command === "email-self") {
          this.sendPDF();
        }
      }
    },

    components: {
      AccessControlPanel
    },
  }
</script>

<style scoped>
    .share_button_left_margin {
        margin-left: 10px;
    }

    .errorMsg {
        margin-bottom: 18px;
    }
</style>
