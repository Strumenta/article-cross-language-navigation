// The module 'vscode' contains the VS Code extensibility API
// Import the module and reference it with the alias vscode in your code below
import * as vscode from 'vscode';
import * as path from "path";
import * as child_process from "child_process";

import { isOSnix } from "./util/os";
import { Status, StatusBarEntry } from './util/status';
import { LanguageClient,ServerOptions,LanguageClientOptions,RevealOutputChannelOn, TextDocumentIdentifier } from "vscode-languageclient";
import axios from 'axios';


var  languageClient : LanguageClient;   

export async function activate(context: vscode.ExtensionContext) : Promise<void> {
	const initTasks: Promise<void>[] = [];
	
	/* Language configuration */
    vscode.languages.setLanguageConfiguration("rpgle", {});
	const lsConfig = vscode.workspace.getConfiguration("rpgle");
	
	initTasks.push(withSpinningStatus(context, async status => {
		await activateLanguageServer(context, status, lsConfig);
	}));


	let gotoGeneratedCode = vscode.commands.registerCommand('language-server.target', async (files?: vscode.Uri[]) => {        
		let fext = extractExtension(vscode.window.activeTextEditor?.document?.uri.fsPath!);
		if(fext == "rpgle") {
            executeCrossReferenceRequest("file://" + vscode.window.activeTextEditor?.document?.uri.fsPath+"/py")
        }            
    } );

		
    let gotoOriginaldCode = vscode.commands.registerCommand('language-server.origin', async (files?: vscode.Uri[]) => {        
        let fext = extractExtension(vscode.window.activeTextEditor?.document?.uri.fsPath!);        
        if(fext == "py") {
            executeCrossReferenceRequest("file://" + vscode.window.activeTextEditor?.document?.uri.fsPath+"/rpgle")
        }                    
    });
        

	context.subscriptions.push(gotoGeneratedCode);
    context.subscriptions.push(gotoOriginaldCode);
	//context.subscriptions.push(disposable);
	await Promise.all(initTasks);
}

async function withSpinningStatus(context: vscode.ExtensionContext, action: (status: Status) => Promise<void>): Promise<void> {
    const status = new StatusBarEntry(context, "$(sync~spin)");
    status.show();
    await action(status);
    status.dispose();
}

async function activateLanguageServer(context: vscode.ExtensionContext, status: Status, config: vscode.WorkspaceConfiguration) { 
	console.log("Activating Language Server...");


    // The language server must be in the bin directory
	const languageServerPath : string =  config.get("languageServer.path")!;
    // The language server must be in the bin directory
    const startScriptPath = path.join( languageServerPath , "bin", "sample-xcode-server" + ((process.platform === 'win32') ? '.bat' : ''));
    console.info("language server path: " + startScriptPath)

    if (isOSnix()) {
        child_process.exec(`chmod +x ${startScriptPath}`);
    }

    const outputChannel = vscode.window.createOutputChannel("RPG");
    context.subscriptions.push(outputChannel);
    
    let env: any = undefined;
    const options = { outputChannel, startScriptPath, env };
    languageClient = createLanguageClient(options);
    
    // Create the language client and start the client.
    let languageClientDisposable = languageClient.start();
    context.subscriptions.push(languageClientDisposable);

    await languageClient.onReady();
}

function createLanguageClient(options: {
    outputChannel: vscode.OutputChannel,
    startScriptPath: string,
    env?: any
}): LanguageClient { 
    
    const clientOptions: LanguageClientOptions = {
        // Register the server for RPG documents
        documentSelector: [
            { language: 'rpgle', scheme: 'file' },
            { language: 'rpgle', scheme: 'rpgle' }
        ],
        synchronize: {
            // Synchronize the setting section 'rpgle' to the server
            // NOTE: this currently doesn't do anything
            configurationSection: 'rpgle',
            // Notify the server about file changes to 'javaconfig.json' files contain in the workspace

            fileEvents: [
                vscode.workspace.createFileSystemWatcher('**/*.rpgle'),
            ]
        },
        outputChannel: options.outputChannel,
        revealOutputChannelOn: RevealOutputChannelOn.Info
    }

    let serverOptions: ServerOptions = {
        command: options.startScriptPath,
        args: [],
        options: {
            cwd: vscode.workspace.workspaceFolders?.[0]?.uri?.fsPath,
            env: options.env
        } 
    }
    console.log("Creating client at {}", options.startScriptPath);
    return new LanguageClient("RPGLE", "RPGLE", serverOptions, clientOptions);
}

function extractExtension(path : String) {
    const pathArray = path.split("/");
    const lastIndex = pathArray.length - 1;
    const fname = pathArray[lastIndex].split("."); 
    return fname[1];
}

async function executeCrossReferenceRequest(uri : string) {
    let curPos = vscode.window.activeTextEditor!.selection.active;
            
    console.log(uri);
    console.log(curPos);

    let param = { 
        textDocument : TextDocumentIdentifier.create(uri),
        position : curPos, 
        partialResultToken : undefined,
        workDoneToken : undefined
    };

    languageClient.sendRequest("textDocument/definition", param)
        .then( 
            (data : any) => {
                console.log(data);
                let fileUri = vscode.Uri.file(data[0].uri)
                let start = new vscode.Position(data[0].range.start.line - 1, data[0].range.start.character)
                let end = new vscode.Position(data[0].range.end.line - 1, data[0].range.end.character)
                vscode.window.showTextDocument(fileUri, { preview: true }).then((editor) => {
                                
                    var range = new  vscode.Range(start,end);                                
                    editor.revealRange(range)
                    editor.selection = new vscode.Selection(range.start,range.end);
        
                    let timerId = setInterval(() => { 
                        clearInterval(timerId);
                        editor.selection = new vscode.Selection(range.start,range.start);                                    
                    }, 1000);
                })
                
                }
            )
        .catch( 
            reason => console.log(reason) );    
    }

// This method is called when your extension is deactivated
export function deactivate() {}
