package com.medKnow.medknow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.medKnow.medknow.ui.screens.ageSelect.AgeSelectScreen
import com.medKnow.medknow.ui.screens.drugSearch.DrugInformationScreen
import com.medKnow.medknow.ui.screens.drugSearch.DrugSearchScreen
import com.medKnow.medknow.ui.screens.firstPage.FirstPageScreen
import com.medKnow.medknow.ui.screens.genderSelect.GenderSelectScreen
import com.medKnow.medknow.ui.screens.healthArticle.ArticleInformationScreen
import com.medKnow.medknow.ui.screens.healthArticle.HealthArticleScreen
import com.medKnow.medknow.ui.screens.login.LoginScreen
import com.medKnow.medknow.ui.screens.medicationPlan.CreateMedicationPlanScreen
import com.medKnow.medknow.ui.screens.medicationPlan.MedicationPlanInformationScreen
import com.medKnow.medknow.ui.screens.medicationPlan.MedicationPlanScreen
import com.medKnow.medknow.ui.screens.medicationPlan.UpdateMedicationPlanScreen
import com.medKnow.medknow.ui.screens.occupationSelect.OccupationSelectScreen
import com.medKnow.medknow.ui.screens.signup.SignupScreen
import com.medKnow.medknow.ui.screens.userPage.CheckUserInformationScreen
import com.medKnow.medknow.ui.screens.userPage.UpdateUserInformationScreen
import com.medKnow.medknow.ui.screens.userPage.UserFeedbackScreen
import com.medKnow.medknow.ui.screens.userPage.UserPageScreen
import com.medKnow.medknow.ui.screens.visitNavigation.VisitNavigationScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    viewModel: StartupViewModel = hiltViewModel()
) {

    // 观察 token
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.FIRST_PAGE else Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGNUP)
                },
                onNavigateToFirstPage = {
                    navController.navigate(Routes.FIRST_PAGE)
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignupScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                },
                onNavigateToAgeSelect = {
                    navController.navigate(Routes.AGE_SELECT)
                }
            )
        }

        composable(Routes.AGE_SELECT) {
            AgeSelectScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToGenderSelect = {
                    navController.navigate(Routes.GENDER_SELECT)
                }
            )
        }

        composable(Routes.GENDER_SELECT) {
            GenderSelectScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToOccupationSelect = {
                    navController.navigate(Routes.OCCUPATION_SELECT)
                }
            )
        }

        composable(Routes.OCCUPATION_SELECT) {
            OccupationSelectScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToFirstPage = {
                    navController.navigate(Routes.FIRST_PAGE)
                }
            )
        }

        composable(Routes.FIRST_PAGE) {
            FirstPageScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToDrugSearch = {
                    navController.navigate(Routes.DRUG_SEARCH)
                },
                onNavigateToHealthArticle = {
                    navController.navigate(Routes.HEALTH_ARTICLE)
                },
                onNavigateToMedicationPlan = {
                    navController.navigate(Routes.MEDICATION_PLAN)
                },
                onNavigateToVisitNavigation = {
                    navController.navigate(Routes.VISIT_NAVIGATION)
                },
                onNavigateToUserPage = {
                    navController.navigate(Routes.USER_PAGE)
                },
                onNavigateToArticleInformation = {
                    navController.navigate(Routes.ARTICLE_INFORMATION)
                }
            )
        }

        composable(Routes.DRUG_SEARCH) {
            DrugSearchScreen(
                onNavigateToFirstPage = {
                    navController.navigate(Routes.FIRST_PAGE)
                },
                onNavigateToDrugInformation = {
                    navController.navigate(Routes.DRUG_INFORMATION)
                }
            )
        }

        composable(Routes.DRUG_INFORMATION) {
            DrugInformationScreen(
                onNavigateToDrugSearch = {
                    navController.navigate(Routes.DRUG_SEARCH)
                }
            )
        }

        composable(Routes.HEALTH_ARTICLE) {
            HealthArticleScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToArticleInformation = {
                    navController.navigate(Routes.ARTICLE_INFORMATION)
                },
                onNavigateToFirstPage = {
                    navController.navigate(Routes.FIRST_PAGE)
                },
                onNavigateToMedicationPlan = {
                    navController.navigate(Routes.MEDICATION_PLAN)
                },
                onNavigateToVisitNavigation = {
                    navController.navigate(Routes.VISIT_NAVIGATION)
                },
                onNavigateToUserPage = {
                    navController.navigate(Routes.USER_PAGE)
                }
            )
        }

        composable(Routes.ARTICLE_INFORMATION) {
            ArticleInformationScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToHealthArticle = {
                    navController.navigate(Routes.HEALTH_ARTICLE)
                }
            )
        }

        composable(Routes.CREATE_MEDICATION_PLAN) {
            CreateMedicationPlanScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToMedicationPlan = {
                    navController.navigate(Routes.MEDICATION_PLAN)
                }
            )
        }

        composable(Routes.MEDICATION_PLAN_INFORMATION) {
            MedicationPlanInformationScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToMedicationPlan = {
                    navController.navigate(Routes.MEDICATION_PLAN)
                }
            )
        }

        composable(route = Routes.UPDATE_MEDICATION_PLAN) {
            UpdateMedicationPlanScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToMedicationPlan = {
                    navController.navigate(Routes.MEDICATION_PLAN)
                }
            )
        }

        composable(Routes.MEDICATION_PLAN) {
            MedicationPlanScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToCreateMedicationPlan = {
                    navController.navigate(Routes.CREATE_MEDICATION_PLAN)
                },
                onNavigateToMedicationInformation = {
                    navController.navigate(Routes.MEDICATION_PLAN_INFORMATION)
                },
                onNavigateToUpdateMedicationPlan = {
                    navController.navigate(Routes.UPDATE_MEDICATION_PLAN)
                },
                onNavigateToFirstPage = {
                    navController.navigate(Routes.FIRST_PAGE)
                },
                onNavigateToHealthArticle = {
                    navController.navigate(Routes.HEALTH_ARTICLE)
                },
                onNavigateToVisitNavigation = {
                    navController.navigate(Routes.VISIT_NAVIGATION)
                },
                onNavigateToUserPage = {
                    navController.navigate(Routes.USER_PAGE)
                }
            )
        }

        composable(Routes.USER_PAGE) {
            UserPageScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToCheckUserInformation = {
                    navController.navigate(Routes.CHECK_USER_INFORMATION)
                },
                onNavigateToUpdateUserInformation = {
                    navController.navigate(Routes.UPDATE_USER_INFORMATION)
                },
                onNavigateToUserFeedback = {
                    navController.navigate(Routes.USER_FEEDBACK)
                },
                onNavigateToFirstPage = {
                    navController.navigate(Routes.FIRST_PAGE)
                },
                onNavigateToHealthArticle = {
                    navController.navigate(Routes.HEALTH_ARTICLE)
                },
                onNavigateToVisitNavigation = {
                    navController.navigate(Routes.VISIT_NAVIGATION)
                },
                onNavigateToMedicationPlan = {
                    navController.navigate(Routes.MEDICATION_PLAN)
                }
            )
        }

        composable(Routes.CHECK_USER_INFORMATION) {
            CheckUserInformationScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToUserPage = {
                    navController.navigate(Routes.USER_PAGE)
                }
            )
        }

        composable(Routes.UPDATE_USER_INFORMATION) {
            UpdateUserInformationScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToUserPage = {
                    navController.navigate(Routes.USER_PAGE)
                }
            )
        }

        composable(Routes.USER_FEEDBACK) {
            UserFeedbackScreen(
                onNavigateToHome = {
                    navController.navigate(Routes.FIRST_PAGE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToUserPage = {
                    navController.navigate(Routes.USER_PAGE)
                }
            )
        }

        composable(Routes.VISIT_NAVIGATION) {
            VisitNavigationScreen(
                onNavigateToFirstPage = {
                    navController.navigate(Routes.FIRST_PAGE)
                },
                onNavigateToHealthArticle = {
                    navController.navigate(Routes.HEALTH_ARTICLE)
                },
                onNavigateToUserPage = {
                    navController.navigate(Routes.USER_PAGE)
                },
                onNavigateToMedicationPlan = {
                    navController.navigate(Routes.MEDICATION_PLAN)
                }
            )
        }

    }

}