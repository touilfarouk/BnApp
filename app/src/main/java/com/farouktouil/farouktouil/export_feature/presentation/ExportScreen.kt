package com.farouktouil.farouktouil.export_feature.presentation

import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel

import java.io.File
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    exportViewModel: ExportViewModel = hiltViewModel()
) {

    val fileExportState = exportViewModel.fileExportState
    val context = LocalContext.current
    val availableStructures = exportViewModel.availableStructures
    val selectedStructureName = exportViewModel.selectedStructureName

    // Load available structures when screen is first shown
    LaunchedEffect(Unit) {
        exportViewModel.loadAvailableStructures()
    }

    LaunchedEffect(key1 = fileExportState){
        if(fileExportState.isShareDataClicked){
            val uri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName+".provider",
                File(fileExportState.shareDataUri!!)
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/csv"
            intent.putExtra(Intent.EXTRA_SUBJECT,"My Export Data")
            intent.putExtra(Intent.EXTRA_STREAM,uri)

            val chooser = Intent.createChooser(intent,"Share With")
            ContextCompat.startActivity(
                context,chooser,null
            )
            exportViewModel.onShareDataOpen()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
            //.background(gray),
        contentAlignment = Alignment.Center
    ){
        Column(

            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp),
        ){
            // Structure Selection Dropdown
            var expanded by remember { mutableStateOf(false) }
            val selectedStructure = selectedStructureName

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                OutlinedTextField(
                    value = selectedStructure ?: "Toutes les structures",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sélectionner une structure") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1B5E20), // BNEDER primary green
                        focusedLabelColor = Color(0xFF1B5E20), // BNEDER primary green
                        cursorColor = Color(0xFF1B5E20) // BNEDER primary green
                    ),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)

                    },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // "All structures" option
                    DropdownMenuItem(
                        text = { Text("Toutes les structures") },
                        onClick = {
                            exportViewModel.setSelectedStructure(null)
                            expanded = false
                        }
                    )

                    // Individual structure options
                    availableStructures.forEach { structure ->
                        DropdownMenuItem(
                            text = { Text(structure) },
                            onClick = {
                                exportViewModel.setSelectedStructure(structure)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Collected data amount: ${exportViewModel.collectedDataAmount}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
              //  color = white,
                textAlign = TextAlign.Center
            )
            IconButton(
                onClick = { exportViewModel.generateExportFile() },
                enabled = !fileExportState.isSharedDataReady
            ) {
                Icon(
                    imageVector = Icons.Default.IosShare, // Export file icon
                    contentDescription = "Export File",
                    tint = Color(0xFF1B5E20) // BNEDER primary green
                )
            }

            AnimatedVisibility(visible = fileExportState.isSharedDataReady) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Export succès",
                        tint = Color(0xFF1B5E20)
                    )

                    Text(
                        text = "Export réussi !",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Fichier enregistré dans Téléchargements/OrderApp.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = {
                            val initialUri =
                                "content://com.android.externalstorage.documents/document/primary:Download/OrderApp".toUri()
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                                putExtra(DocumentsContract.EXTRA_INITIAL_URI, initialUri)
                                addFlags(
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                                )
                            }
                            ContextCompat.startActivity(
                                context,
                                Intent.createChooser(intent, "Ouvrir le dossier"),
                                null
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5E20),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Ouvrir le dossier")
                    }
                }
            }
        }
    }

    if (fileExportState.isGeneratingLoading){
        Dialog(
            onDismissRequest = {}
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                CircularProgressIndicator(
                   // color = white
                )
                Text(
                    "Generating File (${fileExportState.generatingProgress}%) ...",
                  //  color = white,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }


}
